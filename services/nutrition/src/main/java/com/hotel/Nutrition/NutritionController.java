
package com.hotel.Nutrition;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class NutritionController {

    // private final NutritionService nutritionService;
    private final JdbcTemplate jdbcTemplate;
    private final NutritionRepository nutritionRepository;

    // @Autowired
    // public NutritionController(NutritionService nutritionService) {
    //     this.nutritionService = nutritionService;
    // }

    NutritionController(NutritionRepository nutritionRepository, JdbcTemplate jdbcTemplate){
        this.nutritionRepository = nutritionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @GetMapping("/test")
    //We wanna add this since client is in dif domain
    @CrossOrigin(origins = "http://localhost:3000")
    public String test() {
        return "Hello, this is a test!";
    }

    @GetMapping("/getFoods") //get all foods in db 
    public List<Nutrition> food() {
        String query = """
                SELECT *
                FROM `sampleFoods`
            """;
        return jdbcTemplate.query(query, new NutritionMapper());
    }

    // @GetMapping("/food/{name}") //get food by name and return its nutrition info
    // public Nutrition foodByName(@PathVariable String name) {
    //     String query = """
    //         SELECT *
    //         FROM `sampleFoods`
    //         WHERE name = %s
    //     """;
    //     String SQL = String.format(query, name);

    //     List<Nutrition> temp = jdbcTemplate.query(SQL, new NutritionMapper());
    //     return temp.get(0);
    // }

    @GetMapping("/food/{name}") //get food by name and return its nutrition info
    public List<Nutrition> foodByName(@PathVariable String name) {
        String query = """
            SELECT *
            FROM `sampleFoods`
            WHERE name LIKE '%%%s%%'
        """;
        String SQL = String.format(query, name);

        return jdbcTemplate.query(SQL, new NutritionMapper());
    }

    // @PostMapping("/addFood/{name}/{calories}/{protein}/{carbs}/{fat}") //test adding a food to the db
    // public void addFood(
    //     @PathVariable String foodName,
    //     @PathVariable int calories, 
    //     @PathVariable int protein,
    //     @PathVariable int carbs, 
    //     @PathVariable int fat) {
    //     // nutritionRepository.addFood(foodName, calories, protein, carbs, fat);

    //     String query = """
    //         INSERT INTO `sampleFoods` (name, calories, protein, carbs, fat)
    //         VALUES ('%s', %d, %d, %d, %d)
    //     """;    
    //     String SQL = String.format(query, foodName, calories, protein, carbs, fat);
    //     jdbcTemplate.update(SQL);
    // }

   

}
    


