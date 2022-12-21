package com.abhiyan.bookrentalsystem.service.impl;

import com.abhiyan.bookrentalsystem.converter.BookDtoConverter;
import com.abhiyan.bookrentalsystem.dto.BookDto;
import com.abhiyan.bookrentalsystem.dto.ResponseDto;
import com.abhiyan.bookrentalsystem.model.Author;
import com.abhiyan.bookrentalsystem.model.Book;
import com.abhiyan.bookrentalsystem.model.Category;
import com.abhiyan.bookrentalsystem.repository.AuthorRepo;
import com.abhiyan.bookrentalsystem.repository.BookRepo;
import com.abhiyan.bookrentalsystem.repository.CategoryRepo;
import com.abhiyan.bookrentalsystem.service.BookService;
import com.abhiyan.bookrentalsystem.service.services.StringToDate;
import com.abhiyan.bookrentalsystem.utils.FileStorageUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final BookDtoConverter bookDtoConverter;

    private final CategoryRepo categoryRepo;

    private final AuthorRepo authorRepo;
    private final FileStorageUtils fileStorageUtils;
    public BookServiceImpl(BookRepo bookRepo, BookDtoConverter bookDtoConverter, CategoryRepo categoryRepo,
                           AuthorRepo authorRepo, FileStorageUtils fileStorageUtils) {
        this.bookRepo = bookRepo;
        this.bookDtoConverter = bookDtoConverter;
        this.categoryRepo = categoryRepo;
        this.authorRepo = authorRepo;
        this.fileStorageUtils = fileStorageUtils;
    }

    @Override
    public ResponseDto saveBookDetails(BookDto bookDto) throws ParseException, IOException {
        Book book = new Book();


        try{
            book.setName(bookDto.getName());
            book.setNoOfPages(bookDto.getNoOfPages());
            book.setIsbn(bookDto.getIsbn());
            book.setRating(bookDto.getRating());

            StringToDate sDate = new StringToDate();
            LocalDate date = sDate.StringToDate(bookDto.getPublishedDate());
            book.setPublishedDate(date);
//        long timeStamp = System.currentTimeMillis();

            book.setStockCount(bookDto.getStockCount());

            //file
            MultipartFile multipartFile = bookDto.getImageFile();
            String filePath = fileStorageUtils.storeFile(multipartFile);
            book.setFilePath(filePath);

            Category category = categoryRepo.findById(bookDto.getCategoryId()).orElse(null);
            book.setCategory(category);

            List<Author> authors = authorRepo.findAllById(bookDto.getAuthorId());
            book.setAuthors(authors);
            bookRepo.save(book);

            return ResponseDto.builder()
                    .message("Book added successfully.")
                    .status(true)
                    .build();

        }
        catch(Exception e){
            if(e.getMessage().contains("isbn")){
                return ResponseDto.builder()
                        .message("Book already registered with the ISBN number")
                        .status(false)
                        .build();
            }

            else{
                e.printStackTrace();
                return ResponseDto.builder()
                        .message(e.getMessage())
                        .status(false)
                        .build();
            }
        }

    }

    @Override
    public List<BookDto> getAllBooks() {
        List<Book> book = bookRepo.findAll();
        List<BookDto> bookDto = bookDtoConverter.entityToDto(book);
        return bookDto;
    }

    @Override
    public BookDto editBook(Integer id) {
        Book book = bookRepo.findById(id).orElse(null);
        BookDto bookDto = bookDtoConverter.entityToDto(book);
        return bookDto;
    }

    @Override
    public BookDto updateBook(Integer id, BookDto bookDto) throws ParseException {
        Book book = bookRepo.findById(id).orElse(null);
        book.setName(bookDto.getName());
        book.setIsbn(bookDto.getIsbn());
        Category category = categoryRepo.findById(bookDto.getCategoryId()).orElse(null);
        System.out.println(bookDto.getCategoryId());
        book.setCategory(category);
        book.setRating(bookDto.getRating());
        book.setNoOfPages(bookDto.getNoOfPages());
//        book.setPhoto(bookDto.getPhoto());
        book.setStockCount(bookDto.getStockCount());

        StringToDate sDate = new StringToDate();
        LocalDate date = sDate.StringToDate(bookDto.getPublishedDate());
        book.setPublishedDate(date);
        //        book.setAuthors(bookDto.getAuthor());

        List<Author> authors = authorRepo.findAllById(bookDto.getAuthorId());
        book.setAuthors(authors);
        bookRepo.save(book);
        bookRepo.save(book);
        return bookDto;
    }

    @Override
    public void deleteBookById(Integer id) {
        bookRepo.deleteById(id);
    }

    @Override
    public List<BookDto> findAllBooksWithStock() {
        List<Book> book = bookRepo.findAllBookWithStock();
        List<BookDto> bookDtos = bookDtoConverter.entityToDto(book);
        return bookDtos;
    }
}
