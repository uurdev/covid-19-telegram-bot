package com.covid.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ValidateController {

    @RequestMapping("/")
    @ResponseBody
    public String validateBot(){
        return "Telegram bot is running :)";
    }
}
