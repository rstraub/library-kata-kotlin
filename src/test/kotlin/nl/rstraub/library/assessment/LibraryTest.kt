package nl.rstraub.library.assessment

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe

internal class LibraryTest : WordSpec({
    lateinit var library: Library
    lateinit var member: Member
    lateinit var book: Book

    beforeEach {
        book = Book("1984", "1")
        member = Member("george")
        library = Library(book)
    }

    "inventory" should {
        "return empty list given zero books" {
            Library().inventory() shouldBe emptyList()
        }

        "return all books in the library" {
            library = Library(
                book,
                Book("picture of dorian gray", "1")
            )

            val result = library.inventory()

            result shouldContain book
            result shouldContain Book("picture of dorian gray", "1")
        }
    }

    "addBook" should {
        "add a book to the inventory" {
            val bookToAdd = Book("the odyssey", "1")

            library add bookToAdd
            val result = library.inventory()

            result shouldHaveSize 2
            result shouldContainAll listOf(book, bookToAdd)
        }

        "add duplicate books to the inventory" {
            val otherCopy = book.copy(serialCode = "2")
            library add otherCopy
            val result = library.inventory()

            result shouldHaveSize 2
            result shouldContain book
            result shouldContain otherCopy
        }
    }

    "addMember" should {
        "add a member to the library" {
            library add member
            val result = library.members()

            result shouldHaveSize 1
            result shouldContain member
        }

        "not add a duplicate member to the library" {
            library add member
            library add member
            val result = library.members()

            result shouldContain member
            result.shouldNotContainDuplicates()
        }
    }

    "lend" should {
        beforeEach {
            library add member
        }

        "return false if the book is not in the library" {
            library.lend(Book("404", "1"), member) shouldBe false

            member.loanedBooks().shouldBeEmpty()
        }

        "return false if the book is already loaned out" {
            library.lend(book, member)

            library.lend(book, member) shouldBe false
        }

        "return false if the member is not a member of the library" {
            library.lend(book, Member("gorge")) shouldBe false

            library.inventory() shouldContain book
        }

        "return false if the member already has seven loaned books" {
            member.loanBook(Book("1", "1"))
            member.loanBook(Book("2", "1"))
            member.loanBook(Book("3", "1"))
            member.loanBook(Book("4", "1"))
            member.loanBook(Book("5", "1"))
            member.loanBook(Book("6", "1"))
            member.loanBook(Book("7", "1"))

            library.lend(book, member) shouldBe false

            member.loanedBooks() shouldNotContain book
        }

        "return true given the book passed from the library to the member" {
            library.lend(book, member) shouldBe true

            member.loanedBooks() shouldContain book
        }
    }

    "returnBook" should {
        beforeEach {
            library add member
        }

        "return true if the book was returned to the library and removed from the member" {
            library.lend(book, member)

            library.returnBook(book, member) shouldBe true

            member.loanedBooks() shouldNotContain book
        }

        "return false if the member does not have the book in his possession" {
            library.returnBook(book, member) shouldBe false
        }

        "return false if the member is not a member of the library" {
            val unknownMember = Member("john doe")
            unknownMember.loanBook(book)

            library.returnBook(book, unknownMember) shouldBe false
        }

        "return false if the book is not property of the library" {
            val unknownBook = Book("Twilight", "1")
            member loanBook unknownBook

            library.returnBook(unknownBook, member) shouldBe false
        }
    }

    "removeBook" should {
        beforeEach {
            library add member
        }

        "return true if the book is available and remove it from the inventory" {
            library remove book shouldBe true
            library.inventory() shouldNotContain book
        }

        "return false and leave the book if it is currently loaned out" {
            library.lend(book, member) shouldBe true

            library remove book shouldBe false
            library.inventory() shouldContain book
        }

        "return false if the book is not property of the library" {
            val unknownBook = Book("Twilight", "1")

            library remove unknownBook shouldBe false
        }
    }

    "getMemberLoaning" should {
        "return nothing if the book is in the library inventory" {
        }

        "return the member currently loaning the book" {

        }
    }
})
