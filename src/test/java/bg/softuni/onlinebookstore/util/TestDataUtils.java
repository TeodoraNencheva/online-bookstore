package bg.softuni.onlinebookstore.util;

import bg.softuni.onlinebookstore.model.entity.*;
import bg.softuni.onlinebookstore.model.enums.UserRoleEnum;
import bg.softuni.onlinebookstore.repositories.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class TestDataUtils {
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;
    private GenreRepository genreRepository;

    public TestDataUtils(UserRepository userRepository, UserRoleRepository userRoleRepository, AuthorRepository authorRepository, BookRepository bookRepository, GenreRepository genreRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
    }

    public void initRoles() {
        if (userRoleRepository.count() == 0) {
            UserRoleEntity adminRole = new UserRoleEntity(UserRoleEnum.ADMIN);
            UserRoleEntity userRole = new UserRoleEntity(UserRoleEnum.USER);

            userRoleRepository.save(adminRole);
            userRoleRepository.save(userRole);
        }
    }

    public UserEntity createTestAdmin(String email) {

        initRoles();

        var admin = new UserEntity("Admin", "Adminov", email, "adminPass",
                new HashSet<>(userRoleRepository.findAll()));

        return userRepository.save(admin);
    }

    public UserEntity createTestUser(String email) {

        initRoles();

        var user = new UserEntity("User", "Userov", email, "userPass",
                userRoleRepository.
                        findAll().stream().
                        filter(r -> r.getName() != UserRoleEnum.ADMIN).
                        collect(Collectors.toSet()));

        return userRepository.save(user);
    }

    public AuthorEntity createTestAuthor() {
        var author = new AuthorEntity();
        author.setFirstName("Ivan");
        author.setLastName("Vazov");
        author.setBiography("short biography");
        author.setPhotoUrl("img url");
        return authorRepository.save(author);
    }

    public GenreEntity createTestGenre(String name) {
        GenreEntity genreEntity = new GenreEntity(name);
        return genreRepository.save(genreEntity);
    }

    public BookEntity createTestBook(AuthorEntity author, GenreEntity genre) {
        BookEntity book = new BookEntity("title", author, genre, "year", "summary", "image url", new BigDecimal(10));
        return bookRepository.save(book);
    }

    public Long getAddedBookId() {
        return bookRepository.findAll().stream()
                .max(Comparator.comparingLong(BookEntity::getId)).get().getId();
    }

    public void cleanUpDatabase() {
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }
}