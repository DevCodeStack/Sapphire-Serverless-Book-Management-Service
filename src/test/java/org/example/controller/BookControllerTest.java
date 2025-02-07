package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.example.dto.CreateBook;
import org.example.model.Book;
import org.example.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(1l, "Spring Boot Basics", "John Doe", 25.99);

        book2 = new Book(2l, "Advanced Java", "Jane Doe", 30.50);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void testGetAllBooks() throws Exception {
        List<Book> books = Arrays.asList(book1, book2);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Spring Boot Basics"))
                .andExpect(jsonPath("$[1].title").value("Advanced Java"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById_Found() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book1));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Basics"))
                .andExpect(jsonPath("$.author").value("John Doe"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/3"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getBookById(3L);
    }

    @Test
    void testCreateBook() throws Exception {
    	CreateBook cbook = new CreateBook();
        cbook.setTitle(book1.getTitle());
        cbook.setAuthor(book1.getAuthor());
        cbook.setPrice(book1.getPrice());
    	
        when(bookService.saveBook(any(CreateBook.class))).thenReturn(book1);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cbook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Basics"))
                .andExpect(jsonPath("$.author").value("John Doe"));

        verify(bookService, times(1)).saveBook(any(CreateBook.class));
    }

    @Test
    void testUpdateBook_Success() throws Exception {
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(book1);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(book1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Basics"))
                .andExpect(jsonPath("$.author").value("John Doe"));

        verify(bookService, times(1)).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void testUpdateBook_NotFound() {
        try {
        	when(bookService.updateBook(eq(3L), any(Book.class))).thenThrow(new RuntimeException("Book not found"));

            mockMvc.perform(put("/books/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(book1)))
                    .andExpect(status().isInternalServerError());
        } catch (Exception ex) {
        	verify(bookService, times(1)).updateBook(eq(3L), any(Book.class));
        }
    }

    @Test
    void testDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).deleteBook(1L);
    }
}

