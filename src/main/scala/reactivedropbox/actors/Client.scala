package reactivedropbox.actors

import com.dropbox.core._
import reactivedropbox.core._
import java.io.{FileOutputStream, File, FileInputStream}
import reactivedropbox.core.Entry
import reactivedropbox.core.LocalFile
import scala.concurrent.Future
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import akka.actor.TypedActor
import reactivedropbox.core.Entry
import reactivedropbox.core.SearchResult
import reactivedropbox.core.LocalFile
import reactivedropbox.core.DirectoryListing

/**
 * Client interface used for typed actor
 */
trait Client {

  /**
   * Upload a file
   *
   * @param localPath local path
   * @param remotePath remote path
   * @param mode writeMode
   * @return Entry
   */
  def upload(localPath: String, remotePath: String, mode: DbxWriteMode): Future[Entry]

  /**
   * Download a file
   *
   * @param localPath local path
   * @param remotePath remote path
   * @param revision revision
   */
  def download(localPath: String, remotePath: String, revision: Option[String] = None): Future[LocalFile]

  /**
   * List the files
   *
   * @param remotePath remote path
   */
  def listFiles(remotePath: String): Future[DirectoryListing]

  /**
   * Search for files which match the query
   *
   * @param query criteria
   * @param path path where to search, it will recurse into child items
   */
  def search(query: String, path: String = "/"): Future[SearchResult]

  /**
   * Poll for changes
   *
   * @param interval
   * @param f
   * @return
   */
  def poll(interval: FiniteDuration = 5.minutes)(f: Any => Unit)
}

/**
 * Client Implementation
 *
 * @param config Configuration
 * @param accessToken Access Token
 */
class DefaultClient(config: DbxRequestConfig, accessToken: String) extends Client {

  import scala.concurrent.ExecutionContext.Implicits.global

  val client = new DbxClient(config, accessToken)
  var deltaCursor: String = null
  var cache = List[DeltaItem]()

  /**
   * Upload a file to dropbox
   *
   * @param localPath local file which to upload
   * @param remotePath remote path where to upload
   * @param mode writeMode
   */
  def upload(localPath: String, remotePath: String, mode: DbxWriteMode) = Future {
    val inputFile = new File(localPath)
    val inputStream = new FileInputStream(inputFile)
    val uploadedFile = client.uploadFile(remotePath, mode, inputFile.length(), inputStream)
    Entry(uploadedFile)
  }

  /**
   * Download a file
   * @param remoteFile Remote location where to download the file
   * @param localFile Local location where to place the new file
   * @return New local file
   */
  def download(remoteFile: String, localFile: String, revision: Option[String] = None) = Future {
    val outputStream = new FileOutputStream(localFile)
    try {
      val downloadedFile = client.getFile(remoteFile, revision.getOrElse(null), outputStream)
      LocalFile(localFile, downloadedFile)
    } finally {
      outputStream.close()
    }
  }

  /**
   * Remote path
   * @param remotePath Path
   */
  def listFiles(remotePath: String) = Future {
    DirectoryListing(client.getMetadataWithChildren(remotePath), remotePath)
  }

  /**
   * Search for files which match the query
   *
   * @param query criteria
   * @param path path where to search, it will recurse into child items
   */
  def search(query: String, path: String) = Future {
    SearchResult(client.searchFileAndFolderNames(path, query).toList, path)
  }

  /**
   * Refresh the cache using delta
   *
   * @return
   */
  private def refresh() = {
    var hasMore = true
    while(hasMore) {
      val result = client.getDelta(deltaCursor)
      if (result.reset) cache = List()
      deltaCursor = result.cursor
      hasMore = result.hasMore
      result.entries.map(x => DeltaItem(x.lcPath, x.metadata)).map {
        case item @ DeltaItem(path, null)   => cache = cache.filterNot(x => x.path.toLowerCase == path)
        case item @ DeltaItem(path, entry)  => cache ::= item
      }
    }
  }

  /**
   * Poll for changes
   *
   * @param interval
   * @param f
   * @return
   */
  def poll(interval: FiniteDuration = 5.minutes)(f: Any => Unit) = {
    println("start polling!")
    var pollCache = cache.toList
    TypedActor.context.system.scheduler.schedule(interval, interval) {
      refresh()
      // Find all added entries
      cache.filterNot(pollCache.toSet).foreach {
        case DeltaItem(path, entry) => f(EntryAdded(entry))
      }
      // Find all removed entries
      pollCache.filterNot(cache.toSet).foreach {
        case DeltaItem(path, entry) => f(EntryRemoved(entry))
      }
      pollCache = cache
    }
  }
}

case class DeltaItem(path: String, entry: DbxEntry)