package wonbin.calculator.Entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SalaryRequest {
    private int usernumber;
    private int year;
    private int month;
    private List<ScheduleViewInfo> workLogs;
    private List<holidayInfo> holidayList;
    private int prev_weekly_bonus;
}
