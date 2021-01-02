package nl.rstraub.library.assessment

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.shouldBe

internal class LibraryTest : WordSpec({
    lateinit var library: Library
    lateinit var member: Member
    lateinit var book: Book

    beforeEach {
        book = Book("1984")
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
                Book("picture of dorian gray")
            )

            val result = library.inventory()

            result shouldContain book
            result shouldContain Book("picture of dorian gray")
        }
    }

    "addBook" should {
        "add a book to the inventory" {
            val bookToAdd = Book("the odyssey")

            library add bookToAdd
            val result = library.inventory()

            result shouldHaveSize 2
            result shouldContainAll listOf(book, bookToAdd)
        }

        "add duplicate books to the inventory" {
            library add book
            val result = library.inventory()

            result shouldHaveSize 2
            result shouldContain book
            result.shouldContainDuplicates()
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
            library.lend(Book("404"), member) shouldBe false

            member.loanedBooks().shouldBeEmpty()
        }

        "return false if the member is not a member of the library" {
            library.lend(book, Member("gorge")) shouldBe false

            library.inventory() shouldContain book
        }

        "return false if the member already has seven loaned books" {
            member.loanBook(Book("1"))
            member.loanBook(Book("2"))
            member.loanBook(Book("3"))
            member.loanBook(Book("4"))
            member.loanBook(Book("5"))
            member.loanBook(Book("6"))
            member.loanBook(Book("7"))

            library.lend(book, member) shouldBe false

            library.inventory() shouldContain book
            member.loanedBooks() shouldNotContain book
        }

        "return true given the book passed from the library to the member" {
            library.lend(book, member) shouldBe true

            member.loanedBooks() shouldContain book
            library.inventory() shouldNotContain book
        }
    }

    "returnBook" should {
        beforeEach {
            member loanBook book
            library add member
        }

        "return true if the book was returned to the library and removed from the member" {
            library.returnBook(book, member) shouldBe true

            library.inventory() shouldContain book
            member.loanedBooks() shouldNotContain book
        }

        "return false if the member does not have the book in his possession" {
            val unknownBook = Book("404")
            library.returnBook(unknownBook, member) shouldBe false

            library.inventory() shouldNotContain unknownBook
        }

        "return false if the member is not a member of the library" {
            val unknownMember = Member("john doe")
            unknownMember.loanBook(book)

            library.returnBook(book, unknownMember) shouldBe false
        }
    }
})
