package com.pantryplan.app;
import java.time.LocalDate;
class ReviewChecks {
 public static void main(String[] args) {
  LocalDate day=LocalDate.of(2028,2,28);
  for (int window : new int[]{1,3,7}) {
   check("", day, window, "UNDATED"); check(null, day, window, "UNDATED");
   check(day.minusDays(1).toString(),day,window,"PASSED");
   check(day.toString(),day,window,"UPCOMING");
   check(day.plusDays(window).toString(),day,window,"UPCOMING");
   check(day.plusDays(window+1).toString(),day,window,"LATER");
  }
  System.out.println("Passed 18 date boundary checks.");
 }
 static void check(String date, LocalDate today,int window,String expected) {
  if (!PantryReview.group(date,today,window).name().equals(expected)) throw new AssertionError(date);
 }
}
