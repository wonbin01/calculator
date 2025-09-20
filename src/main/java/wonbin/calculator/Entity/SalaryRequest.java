package wonbin.calculator.Entity;

import lombok.Data;

import java.util.List;

@Data
public class SalaryRequest {
    private int usernumber;
    private int year;
    private int month;
    private List<ScheduleViewInfo> workLogs;
}
