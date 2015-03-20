package pl.tk.warszawscala.slickday.web.service

import java.time.{ZonedDateTime, LocalDateTime}
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

import pl.tk.warszawscala.slickday.web.http.model._
import pl.tk.warszawscala.slickday.web.repository.{MockLibraryrRepositoryComponent, LibraryRepositoryComponent}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by tomaszk on 3/19/15.
 */

trait MockLibraryServiceComponent extends LibraryServiceComponent with MockLibraryrRepositoryComponent {


  override val getLibraryService: NoteService = new MockNoteService

//  override val getLibraryRepository = new MockLibraryrRepositoryComponent {}

  private class MockNoteService extends NoteService {

    override def save(author: Author): Future[Long] = Future.successful(getLibraryRepository.authorRepo.persist(author))

    override def save(book: Book): Future[Long] = Future.successful(getLibraryRepository.bookRepo.persist(book))

    override def save(category: Category): Future[Long] = Future.successful(getLibraryRepository.categoryRepo.persist(category))

    override def findAuthorById(id: Long): Future[Option[Author]] = Future.successful(getLibraryRepository.authorRepo.get(id))

    override def findCategoryById(id: Long): Future[Option[Category]] = Future.successful(getLibraryRepository.categoryRepo.get(id))

    override def findBookById(id: Long): Future[Option[Book]] = Future.successful(getLibraryRepository.bookRepo.get(id))

    override def update(id: Long, entity: Author): Unit = Future.successful(getLibraryRepository.authorRepo.update(id, entity))

    override def update(id: Long, entity: Book): Unit = Future.successful(getLibraryRepository.bookRepo.update(id, entity))

    override def update(id: Long, entity: Category): Unit = Future.successful(getLibraryRepository.categoryRepo.update(id, entity))

    override def getAllBooks(): Future[List[Book]] = Future.successful(getLibraryRepository.bookRepo.store)

    override def getAllCategories(): Future[List[Category]] = Future.successful(getLibraryRepository.categoryRepo.store)

    override def getAllAuthors(): Future[List[Author]] = Future.successful(getLibraryRepository.authorRepo.store)

    override def query(author: Option[String], category: Option[String]): Future[List[Book]] = {
      val meetsPredicate: (Option[String] => String => Boolean) = { p => k =>
        p match {
          case Some(s) => k == s
          case None => true
        }
      }
      Future.successful {
        getLibraryRepository.bookRepo.store.filter { book =>
          book.authors.exists { thisAuthor => meetsPredicate(author)(thisAuthor.name) } &&
            meetsPredicate(category)(book.category.name)
        }
      }

    }
  }
}