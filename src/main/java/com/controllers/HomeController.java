package com.controllers;

import com.file_handling.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.engine.SearchEngine;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private SearchEngine searchEngineService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/search")
    public String searchForm() {
        return "search";
    }

    @PostMapping("/search")
    public String doSearch(@RequestParam("query") String query, Model model) {
        try {
            List<Document> results = searchEngineService.search(query);
            model.addAttribute("results", results);
            model.addAttribute("query", query);
            return "results";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "error";
        }
    }
}