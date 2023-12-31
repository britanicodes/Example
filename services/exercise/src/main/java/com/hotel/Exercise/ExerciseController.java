package com.hotel.Exercise;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Date;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;


@RestController
class ExerciseController {
    private final JdbcTemplate jdbcTemplate;
   //need to get this value from user service
   int userWeight = 65;

    ExerciseController(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostMapping("/LogDiet")
    public void addDiet(@RequestParam String email) {}
    

    @PostMapping("/LogExercise")
    public void addExercise( @RequestParam String email, @RequestParam String type, @RequestParam int length,@RequestParam("date") Date date, @RequestParam String intensity){
      double caloriesBurnt = 0;
      //Calculations for running
      if(type.equals("running") && intensity.equals("low")){
        caloriesBurnt = ( (double) (userWeight * 7)/200) * length;
      }else if(type.equals("running") && intensity.equals("medium")){
        caloriesBurnt = ( (double) (userWeight * 9)/200) * length;
      }else if(type.equals("running") && intensity.equals("high")){
        caloriesBurnt = ( (double) (userWeight * 12)/200) * length;
      }

      //Calculations for walking
      if(type.equals("walking") && intensity.equals("low")){
        caloriesBurnt =  ( (double) (userWeight * 3)/200) * length;
      }else if(type.equals("walking") && intensity.equals("medium")){
        caloriesBurnt = ((double) (userWeight * 4)/200) * length;
      }else if(type.equals("walking") && intensity.equals("high")){
        caloriesBurnt = ( (double) (userWeight * 5)/200) * length;
      }

      //Calculations for swimming
      if(type.equals("swimming") && intensity.equals("low")){
        caloriesBurnt = ( (double) (userWeight * 4)/200) * length;
      }else if(type.equals("swimming") && intensity.equals("medium")){
        caloriesBurnt = ( (double) (userWeight * 7)/200) * length;
      }else if(type.equals("swimming") && intensity.equals("high")){
        caloriesBurnt = ( (double) (userWeight * 10)/200) * length;
      }

      //Calculations for weightlifting
      if(type.equals("weightLifting") && intensity.equals("low")){
        caloriesBurnt = ( (double) (userWeight * 3)/200) * length;
      }else if(type.equals("weightLifting") && intensity.equals("medium")){
        caloriesBurnt = ( (double) (userWeight * 4)/200) * length;
      }else if(type.equals("weightLifting") && intensity.equals("high")){
        caloriesBurnt = ( (double) (userWeight * 5)/200) * length;
      }

      String SQL = "INSERT INTO exercise.exercise_log (email, type, length, date, intensity, caloriesBurnt) VALUES ("+"'"+email+"'"+","+"'"+ type +"'"+" ,"+"'"+length+"'"+","+"'"+date+"','"+intensity+"','"+caloriesBurnt+"')";
      jdbcTemplate.update(SQL);
    }

    @GetMapping("/GetExercise")
    public List<List<String>> getExercise( @RequestParam String email){
      List<List<String>> res = new ArrayList<>();

      // for loop later
      String SQL1 = "SELECT type FROM exercise.exercise_log WHERE email = "+"'"+email+"'"+" ORDER BY date DESC LIMIT 1";
      List<String> temp1 = jdbcTemplate.queryForList(SQL1, String.class);
      String SQL2 = "SELECT length FROM exercise.exercise_log WHERE email = "+"'"+email+"'"+" ORDER BY date DESC LIMIT 1";
      List<String> temp2 = jdbcTemplate.queryForList(SQL2, String.class);
      String SQL3 = "SELECT date FROM exercise.exercise_log WHERE email = "+"'"+email+"'"+" ORDER BY date DESC LIMIT 1";
      List<String> temp3 = jdbcTemplate.queryForList(SQL3, String.class);
      String SQL4 = "SELECT intensity FROM exercise.exercise_log WHERE email = "+"'"+email+"'"+" ORDER BY date DESC LIMIT 1";
      List<String> temp4 = jdbcTemplate.queryForList(SQL4, String.class);

      res.add(temp1);res.add(temp2);res.add(temp3);res.add(temp4);
      return res;
    }

    @GetMapping("/CaloriesBurntOn")
    public int getAvrgCalories( @RequestParam String email, @RequestParam Date day){
      String SQL = "SELECT caloriesBurnt FROM exercise.exercise_log WHERE email = "+"'"+email+"' and date ="+"'"+day+"'"+"";
      List<Integer> temp = jdbcTemplate.queryForList(SQL, Integer.class);
      return temp.get(0);
    }

    @GetMapping("/CaloriesBurnt")
    public List<Integer> getCalories( @RequestParam String email, @RequestParam Date start, @RequestParam Date end){
      String SQL = "SELECT caloriesBurnt FROM exercise.exercise_log WHERE email = "+"'"+email+"'"+"AND date BETWEEN"+"'"+start+"'"+"AND"+"'"+end+"'";
      List<Integer> temp = jdbcTemplate.queryForList(SQL, Integer.class);
      return temp;
    }

    @GetMapping("/AverageCaloriesBurnt")
    public int getAvrgCalories( @RequestParam String email, @RequestParam int days){
      
      String SQL1 = "SELECT caloriesBurnt FROM exercise.exercise_log WHERE email = "+"'"+email+"'"+" ORDER BY date DESC";
      List<Integer> temp = jdbcTemplate.queryForList(SQL1, Integer.class);
      int total = 0;
      for(int i = 0; i < temp.size(); i++){
        total+=temp.get(i);
      }
      return total/days;
    }

    @GetMapping("/predict-fat-loss")
    public int returnFatLossWeight(@RequestParam String email, @RequestParam("date") Date date, @RequestParam int weight) {
      
      return 1000;
    }

    

    @GetMapping("/confirm-user")
    public int confirmLogin(@RequestParam String email, @RequestParam String password) {
    String SQL = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS IsMatch FROM users.userInfo WHERE email = ? AND password = ?";
    int isMatch = jdbcTemplate.queryForObject(SQL, new Object[]{email, password}, Integer.class);
    return isMatch;
  }
}