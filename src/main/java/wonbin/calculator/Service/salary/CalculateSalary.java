package wonbin.calculator.Service.salary;

import org.springframework.stereotype.Service;
import wonbin.calculator.Entity.SalaryInfo;
import wonbin.calculator.Entity.SalaryRequest;
import wonbin.calculator.Entity.ScheduleViewInfo;
import wonbin.calculator.Entity.holidayInfo;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CalculateSalary {
    private static final double TAX_RATE = 0.097; // 세금
    private static final int HOURLY_WAGE = 10030;

    public static SalaryInfo calculate(SalaryRequest request){
        int month= request.getMonth();
        List<ScheduleViewInfo> workLogs=request.getWorkLogs(); //usernumber, 몇일에 일했는지, 언제 출근해서 언제 퇴근했는지
        List<holidayInfo> holidayList = request.getHolidayList(); //date와 name 존재

        Set<LocalDate> holidays=holidayList.stream()
                .map(holidayInfo::getDate)
                .collect(Collectors.toSet());
        double normalHours=0;
        double nightHours=0;
        double holidayHours=0;
        double weeklyHours=0;
        double weeklyPay=0;

        //날짜순 정렬
        workLogs.sort(Comparator.comparing(ScheduleViewInfo::getApplyDate));

        //첫 번째 주의 목요일 구하기
        LocalDate currentWeekStart =workLogs.get(0).getApplyDate();
        while(currentWeekStart.getDayOfWeek()!= DayOfWeek.THURSDAY){
            currentWeekStart=currentWeekStart.minusDays(1);
        }
        LocalDate currentWeekEnd=currentWeekStart.plusDays(6); // 목~수 =7일

        for(ScheduleViewInfo log : workLogs){
            LocalDate date = log.getApplyDate();
            LocalTime startTime=log.getStartTime();
            LocalTime endTime=log.getEndTime();

            if(date.isAfter(currentWeekEnd)){
                if(weeklyHours>=15){
                    weeklyPay+=(weeklyHours/40.0)*8*HOURLY_WAGE;
                }
                weeklyHours=0;
                currentWeekStart=currentWeekStart.plusWeeks(1);
                currentWeekEnd=currentWeekStart.plusDays(6);
            }

            LocalDateTime startDateTime=LocalDateTime.of(date,startTime);
            LocalDateTime endDateTime;

            // 날짜 바뀌는 부분 예외 처리
            if(endTime.isBefore(startTime) || endTime.equals(startTime)){
                endDateTime=LocalDateTime.of(date.plusDays(1),endTime);
            } else {
                endDateTime=LocalDateTime.of(date,endTime);
            }
            Duration worked=Duration.between(startDateTime,endDateTime).minusMinutes(30);
            double hoursWorked=worked.toMinutes()/60.0; //여기까지는 기본 일한 시간 계산

            weeklyHours+=hoursWorked;

            //공휴일 계산
            if(holidays.contains(date)){
                holidayHours+=hoursWorked;
                normalHours+=hoursWorked;
            } else {
                normalHours+=hoursWorked;
            }
            //야간 근무 계산
            LocalDateTime nightStart=LocalDateTime.of(date,LocalTime.of(22,0));
            LocalDateTime nightEnd=LocalDateTime.of(date.plusDays(1),LocalTime.of(6,0));
            LocalDateTime actualNightStart = startDateTime.isAfter(nightStart) ? startDateTime : nightStart;
            LocalDateTime actualNightEnd = endDateTime.isBefore(nightEnd) ? endDateTime : nightEnd;
            if(!actualNightEnd.isBefore(actualNightStart)){
                Duration nightWorked=Duration.between(actualNightStart,actualNightEnd);
                nightHours+=nightWorked.toMinutes()/60.0;
            }
        }
        if(weeklyHours >=15 && currentWeekEnd.getMonthValue()==month){
            weeklyPay+=(weeklyHours/40.0) * 8 * HOURLY_WAGE;
        }
        double basicPay=normalHours * HOURLY_WAGE;
        double nightPay=nightHours*HOURLY_WAGE*0.5;
        double holidayPay=holidayHours*HOURLY_WAGE;
        double totalPay=basicPay +nightPay+holidayPay +weeklyPay;
        double tax=totalPay*TAX_RATE;
        double netpay=totalPay-tax;

        SalaryInfo info=new SalaryInfo();
        info.setDefault_salary((int)basicPay);
        info.setNight_salary((int)nightPay);
        info.setWeekly_bonus((int)weeklyPay);
        info.setHoliday_bonus((int)holidayPay);
        info.setBefore_tax((int)totalPay);
        info.setAfter_tax((int)netpay);
        return info;
    }
}
