package ru.inklumin.inkluminapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.inklumin.inkluminapi.dto.BookResponse;
import ru.inklumin.inkluminapi.dto.SaveBookRequest;
import ru.inklumin.inkluminapi.dto.BookShortInfo;
import ru.inklumin.inkluminapi.entity.User;
import ru.inklumin.inkluminapi.entity.UserBook;
import ru.inklumin.inkluminapi.repository.UserBookRepository;
import ru.inklumin.inkluminapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void testSaveBookData_shouldSaveKind() {
        // Prepare request and user
        Long userId = 1L;
        SaveBookRequest request = new SaveBookRequest();
        request.setUuid("test-uuid");
        request.setBookTitle("Test Book");
        request.setBookData("Some book data");
        request.setKind("novel"); // Set the kind

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        // Mock repository responses
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Mock save to return the passed UserBook, useful for verifying AutoCloseable or other side effects if any
        when(userBookRepository.save(any(UserBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        bookService.saveBookData(userId, request);

        // Capture the argument to save
        ArgumentCaptor<UserBook> bookCaptor = ArgumentCaptor.forClass(UserBook.class);
        verify(userBookRepository).save(bookCaptor.capture());
        UserBook savedBook = bookCaptor.getValue();

        // Assertions
        assertNotNull(savedBook);
        assertEquals("novel", savedBook.getKind());
        assertEquals("test-uuid", savedBook.getUuid());
        assertEquals("Test Book", savedBook.getBookTitle());
        assertEquals(user, savedBook.getUser());
    }

    @Test
    void testGetUserBooks_shouldReturnKindInShortInfo() {
        // Prepare user ID and mock data
        Long userId = 1L;
        String expectedKind = "poetry";
        BookShortInfo mockBookShortInfo = new BookShortInfo("uuid-123", "Poems", expectedKind);
        List<BookShortInfo> expectedShortInfoList = Collections.singletonList(mockBookShortInfo);

        // Mock repository response
        when(userBookRepository.findShortInfoByUserId(userId)).thenReturn(expectedShortInfoList);

        // Call the service method
        List<BookShortInfo> actualShortInfoList = bookService.getUserBooks(userId);

        // Assertions
        assertNotNull(actualShortInfoList);
        assertEquals(1, actualShortInfoList.size());
        BookShortInfo actualBookShortInfo = actualShortInfoList.get(0);
        assertEquals("uuid-123", actualBookShortInfo.getUuid());
        assertEquals("Poems", actualBookShortInfo.getBookTitle());
        assertEquals(expectedKind, actualBookShortInfo.getKind()); // Verify kind

        // Verify repository method was called
        verify(userBookRepository).findShortInfoByUserId(userId);
    }
}
