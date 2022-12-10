package com.abhiyan.bookrentalsystem.controller;

import com.abhiyan.bookrentalsystem.converter.AuthorDtoConverter;
import com.abhiyan.bookrentalsystem.dto.AuthorDto;
import com.abhiyan.bookrentalsystem.model.Author;
import com.abhiyan.bookrentalsystem.service.AuthorServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AuthorController {

    private final AuthorServiceImpl authorService;

    private final AuthorDtoConverter authorDtoConverter;

    public AuthorController(AuthorServiceImpl authorService, AuthorDtoConverter authorDtoConverter) {
        this.authorService = authorService;
        this.authorDtoConverter = authorDtoConverter;
    }


    @GetMapping("/save-author")
    public String saveAuthor(Model model){
        AuthorDto authorDto = new AuthorDto();
        model.addAttribute("authorDto", authorDto);
        return "author/registerAuthor";
    }

    @PostMapping("/save-author/new")
    public String saveAuthor(@Valid @ModelAttribute("authorDto") AuthorDto authorDto,
                             BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            System.out.println("Something went wrong");
            bindingResult.getAllErrors().forEach(a-> System.out.println(a));
            model.addAttribute("authorDto",authorDto );
            return "author/registerAuthor";
        }
        authorService.saveAuthorDetails(authorDto);
        return "redirect:/view-authors";
    }


    @GetMapping("/view-authors")
    public String viewAuthors(Model model){
    List<Author> auth = authorService.getAllAuthors();
    List<AuthorDto> authorDto = authorDtoConverter.entityToDto(auth);
    model.addAttribute("author",authorDto);
    return "author/viewAuthors";
    }

    @GetMapping("/edit-author/{id}")
    public String editAuthor(Model model, @PathVariable int id){
        model.addAttribute("author",authorService.editAuthor(id));
        return "author/updateAuthor";
    }

    @PostMapping("/update-author/{id}")
    public String updateAuthor(Model model, @PathVariable int id,@ModelAttribute AuthorDto authorDto ){
        model.addAttribute("author",authorService.updateAuthor(id,authorDto));
        return "redirect:/view-authors";
    }

    @GetMapping("/delete-author/{id}")
    public String deleteAuthorById(@PathVariable int id){
        authorService.deleteAuthorById(id);
        return "redirect:/view-authors";
    }

}
