package wonbin.calculator.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wonbin.calculator.Entity.SalaryInfo;
import wonbin.calculator.Entity.SalaryRequest;
import wonbin.calculator.Entity.ScheduleViewInfo;

import java.util.List;

@RestController
public class calculatorController {
    @PostMapping("/calculate")
    public void calculate(@RequestBody SalaryRequest request){
        int usernumber= request.getUsernumber();
        int year= request.getYear();
        int month= request.getMonth();
        List<ScheduleViewInfo> workLogs=request.getWorkLogs();
    }
}
