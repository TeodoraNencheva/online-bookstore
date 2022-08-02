package bg.softuni.onlinebookstore.service;

import bg.softuni.onlinebookstore.model.dto.book.*;
import bg.softuni.onlinebookstore.model.entity.AuthorEntity;
import bg.softuni.onlinebookstore.model.entity.BookEntity;
import bg.softuni.onlinebookstore.model.entity.GenreEntity;
import bg.softuni.onlinebookstore.model.mapper.BookMapper;
import bg.softuni.onlinebookstore.repositories.AuthorRepository;
import bg.softuni.onlinebookstore.repositories.BookRepository;
import bg.softuni.onlinebookstore.repositories.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, GenreRepository genreRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookMapper = bookMapper;
    }

    public BookDetailsDTO getBookDetails(Long id) {
        return bookMapper.bookEntityToBookDetailsDTO(bookRepository.findById(id).get());
    }

    public Page<BookOverviewDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::bookEntityToBookOverviewDTO);
    }

    public BookAddedToCartDTO getAddedBook(AddBookToCartDTO bookDTO) {
        BookEntity book = bookRepository.findById(bookDTO.getBookId()).get();
        return new BookAddedToCartDTO(book.getTitle(), book.getAuthor().getFullName(),
                bookDTO.getQuantity());
    }

    public Page<BookOverviewDTO> getBooksByGenre(String genre, Pageable pageable) {
        String genreName = getGenreName(genre);
        return bookRepository.getAllByGenre_Name(genreName, pageable).map(bookMapper::bookEntityToBookOverviewDTO);
    }

    public String getGenreName(String genre) {
        genre = genre.trim().toLowerCase();
        return Arrays.stream(genre.split("_"))
                .map(word -> String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public List<BookOverviewDTO> getBooksByAuthor(Long authorId) {
        return bookRepository.getAllByAuthor_Id(authorId).stream()
                .map(bookMapper::bookEntityToBookOverviewDTO)
                .collect(Collectors.toList());
    }

    public BookEntity addNewBook(AddNewBookDTO bookModel) {
        AuthorEntity author = authorRepository.findById(bookModel.getAuthorId()).get();
        GenreEntity genre = genreRepository.findById(bookModel.getGenreId()).get();
        BookEntity newBook = new BookEntity(bookModel, author, genre);
        return bookRepository.save(newBook);
    }

    public AddNewBookDTO getBookById(Long id) {
        BookEntity book = bookRepository.findById(id).get();
        return bookMapper.bookEntityToAddNewBookDTO(book);
    }

    public BookEntity updateBook(AddNewBookDTO bookModel, Long id) {
        BookEntity book = bookRepository.findById(id).get();
        AuthorEntity author = authorRepository.findById(bookModel.getAuthorId()).get();
        GenreEntity genre = genreRepository.findById(bookModel.getGenreId()).get();

        book.setTitle(bookModel.getTitle());
        book.setAuthor(author);
        book.setGenre(genre);
        book.setYearOfPublication(bookModel.getYearOfPublication());
        book.setSummary(bookModel.getSummary());
        book.setImageUrl(bookModel.getImageUrl());
        book.setPrice(bookModel.getPrice());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
