package com.microsoft.cognitiveservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.microsoft.cognitiveservices.model.PromptRequest;
import com.microsoft.cognitiveservices.service.ContentSafetyService;

import java.util.Map;

@Controller
public class ContentSafetyController {

    private final ContentSafetyService contentSafetyService;

    @Autowired
    public ContentSafetyController(ContentSafetyService contentSafetyService) {
        this.contentSafetyService = contentSafetyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Add an empty PromptRequest object to the model
        model.addAttribute("promptRequest", new PromptRequest());
        return "index";
    }

    @PostMapping("/process")
    public String processPrompt(@ModelAttribute PromptRequest promptRequest, Model model) {
        // Keep the prompt in the model for displaying back to the user
        model.addAttribute("promptRequest", promptRequest);
        
        // Process the prompt through the service
        Map<String, String> result = contentSafetyService.processPrompt(promptRequest.getPrompt());
        
        // Add all result entries to the model
        model.addAllAttributes(result);
        
        return "result";
    }
}