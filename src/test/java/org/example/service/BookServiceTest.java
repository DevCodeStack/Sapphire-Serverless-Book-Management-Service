package org.example.service;

import org.example.model.Book;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(1l, "Spring Boot Basics", "John Doe", 25.99);
        
        book2 = new Book(2l, "Advanced Java", "Jane Doe", 30.50);
    }

    @Test
    void testGetAllBooks() {
        // Mock repository response
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // Service method call
        List<Book> books = bookService.getAllBooks();

        // Assertions
        assertEquals(2, books.size());
        assertEquals("Spring Boot Basics", books.get(0).getTitle());
        assertEquals("Advanced Java", books.get(1).getTitle());

        // Verify interaction with the mock
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> foundBook = bookService.getBookById(1L);

        assertTrue(foundBook.isPresent());
        assertEquals("Spring Boot Basics", foundBook.get().getTitle());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.getBookById(3L);

        assertFalse(foundBook.isPresent());
    }

    @Test
    void testSaveBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book1);
        CreateBook cbook = new CreateBook();
        cbook.setTitle(book1.getTitle());
        cbook.setAuthor(book1.getAuthor());
        cbook.setPrice(book1.getPrice());
        
        Book savedBook = bookService.saveBook(cbook);

        assertNotNull(savedBook);
        assertEquals("Spring Boot Basics", savedBook.getTitle());
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    void testUpdateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        Book updatedBook = Book.builder().title("Updated Title").author("Updated Author").price(40.99).build();
        Book result = bookService.updateBook(1L, updatedBook);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        assertEquals(40.99, result.getPrice());

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> bookService.updateBook(3L, book1));

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }
}

