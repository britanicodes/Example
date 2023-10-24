package com.hotel.Nutrition;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
class ExerciseController {
    private final ExerciseRepository repo;

    ExerciseController(ExerciseRepository repo){
        this.repo = repo;
    }

    @GetMapping("/Hello")
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/AddExercise")
    public void addExercise(@RequestParam String date, @RequestParam String type, @RequestParam int duration, @RequestParam String intensity )  {
        Exercise temp = new Exercise(date, type, duration, intensity);
        repo.save(temp);
        return;
    }
}


