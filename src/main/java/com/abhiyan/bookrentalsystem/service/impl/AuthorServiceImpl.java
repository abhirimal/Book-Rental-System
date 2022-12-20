package com.abhiyan.bookrentalsystem.service.impl;

import com.abhiyan.bookrentalsystem.converter.AuthorDtoConverter;
import com.abhiyan.bookrentalsystem.dto.AuthorDto;
import com.abhiyan.bookrentalsystem.model.Author;
import com.abhiyan.bookrentalsystem.repository.AuthorRepo;
import com.abhiyan.bookrentalsystem.repository.BookRepo;
import com.abhiyan.bookrentalsystem.service.AuthorService;
import com.abhiyan.bookrentalsystem.service.services.EmailSenderService;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepo authorRepo;
    private final AuthorDtoConverter authorDtoConverter;

    private final BookRepo bookRepo;

    private final EmailSenderService emailSenderService;

    public AuthorServiceImpl(AuthorRepo authorRepo, AuthorDtoConverter authorDtoConverter, BookRepo bookRepo, EmailSenderService emailSenderService) {
        this.authorRepo = authorRepo;
        this.authorDtoConverter = authorDtoConverter;
        this.bookRepo = bookRepo;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void saveAuthorDetails(AuthorDto authorDto) throws RuntimeException {

        Author existingAuthor = (Author) authorRepo.findByEmail(authorDto.getEmail()).orElse(null);
        if(existingAuthor!=null){
            throw new RuntimeException("User already exists.");
        }

        Author newAuthor = authorDtoConverter.dtoToEntity(authorDto);

        authorRepo.save(newAuthor);

        // or we can do this way
        // Author newAuthor = new Author();
        // newAuthor.setName(authorDto.getName());
        // newAuthor.setEmail(authorDto.getEmail());
        // newAuthor.setMobile_number(authorDto.getMobile_number());
        // authorRepo.save(newAuthor);

        //send email
        emailSenderService.sendEmail(newAuthor.getEmail(),
                "Hello "+newAuthor.getName()+", \n" +
                        "Your account has been created in Book Rental System \n"+
                "Thank You.",
                "Account created in Book Rental");
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorRepo.findAll();
    }

    @Override
    public AuthorDto editAuthor(Integer id) {
        Author author = authorRepo.findById(id).orElse(null);
        return authorDtoConverter.entityToDto(author);
    }

    @Override
    public AuthorDto updateAuthor(Integer id, AuthorDto authorDto) {
        Author existingAuthor = authorRepo.findById(id).orElse(null);
        existingAuthor.setName(authorDto.getName());
        existingAuthor.setEmail(authorDto.getEmail());
        existingAuthor.setMobileNumber(authorDto.getMobileNumber());
        authorRepo.save(existingAuthor);

        return authorDtoConverter.entityToDto(existingAuthor);
    }

//    @Transactional
    @Override
    public void deleteAuthorById(Integer id) {
        authorRepo.deleteById(id);
//        bookRepo.deleteAuthorBookByAuthorId(id);
//        bookRepo.deleteBookByAuthorId(id);
    }
}