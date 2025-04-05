package com.controllers;

import com.file_handling.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.engine.SearchEngine;

import javax.servlet.http.HttpSession;
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
    public String doSearch(@RequestParam("query") String query,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           Model model,
                           HttpSession session) {
        try {
            List<Document> allResults;

            String cachedQuery = (String) session.getAttribute("lastQuery");
            if (cachedQuery != null && cachedQuery.equals(query)) {
                allResults = (List<Document>) session.getAttribute("lastResults");
                Double cachedTime = (Double) session.getAttribute("lastSearchTimeMs");
                if (cachedTime != null) {
                    model.addAttribute("searchTimeMs", cachedTime);
                }
            } else {
                long startTime = System.nanoTime();
                allResults = searchEngineService.search(query);
                long endTime = System.nanoTime();
                double searchTimeMs = (endTime - startTime) / 1_000_000.0; // in milliseconds

                session.setAttribute("lastQuery", query);
                session.setAttribute("lastResults", allResults);
                session.setAttribute("lastSearchTimeMs", searchTimeMs);

                model.addAttribute("searchTimeMs", searchTimeMs); // Show on first request
            }

            int totalResults = allResults.size();
            int RESULTS_PER_PAGE = 10;
            int startIndex = (page - 1) * RESULTS_PER_PAGE;
            int endIndex = Math.min(startIndex + RESULTS_PER_PAGE, totalResults);
            List<Document> paginatedResults = allResults.subList(startIndex, endIndex);

            model.addAttribute("query", query);
            model.addAttribute("results", paginatedResults);
            model.addAttribute("totalResults", totalResults);
            model.addAttribute("currentPage", page);
            model.addAttribute("hasNextPage", endIndex < totalResults);
            model.addAttribute("hasPrevPage", page > 1);

            return "results";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e);
            return "error";
        }
    }



}