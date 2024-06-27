package com.Xxy.controller;

import com.Xxy.domain.ResponseResult;
import com.Xxy.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LinkControl {
    @Autowired
    private LinkService linkService;

    @GetMapping("getAllLink")
    public ResponseResult getAllLink()
    {
        return linkService.getAllLink();

    }
}
