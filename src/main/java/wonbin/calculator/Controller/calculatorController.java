package wonbin.calculator.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wonbin.calculator.Entity.SalaryInfo;
import wonbin.calculator.Entity.SalaryRequest;
import wonbin.calculator.Service.salary.CalculateSalary;


@RestController
public class calculatorController {
    @PostMapping("/calculate")
    public SalaryInfo calculate(@RequestBody SalaryRequest request){
        return CalculateSalary.calculate(request);
    }
}
