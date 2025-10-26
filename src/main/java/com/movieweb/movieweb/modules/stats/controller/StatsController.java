package com.movieweb.movieweb.modules.stats.controller;


import com.movieweb.movieweb.modules.stats.service.StatsService;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }


}