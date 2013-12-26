package reactivedropbox

import com.dropbox.core.DbxWriteMode
import scala.concurrent.Future
import reactivedropbox.core.{SearchResult, DirectoryListing, LocalFile, Entry}
import scala.concurrent.duration.FiniteDuration
import akka.actor.Cancellable

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
   * @param interval interval between refreshes
   * @param f function which will be run on every refresh
   */
  def poll(interval: FiniteDuration = 5.minutes)(f: Any => Unit): Cancellable
}
