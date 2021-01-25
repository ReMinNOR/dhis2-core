package org.hisp.dhis.commons.action;



import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public abstract class AbstractRelativePeriodsAction
    implements Action
{
    protected boolean reportingMonth;
    protected boolean last3Days;
    protected boolean last7Days;
    protected boolean last14Days;
    protected boolean lastMonth;
    protected boolean reportingWeek;
    protected boolean lastQuarter;
    protected boolean reportingBimonth;
    protected boolean lastBiMonth;
    protected boolean reportingQuarter;
    protected boolean last6Months;
    protected boolean lastSixMonth;
    protected boolean reportingSixMonth;
    protected boolean weeksThisYear;
    protected boolean monthsThisYear;
    protected boolean biMonthsThisYear;
    protected boolean quartersThisYear;
    protected boolean thisYear;
    protected boolean reportingDay;
    protected boolean monthsLastYear;
    protected boolean quartersLastYear;
    protected boolean last5Years;
    protected boolean lastYear;
    protected boolean last4Quarters;
    protected boolean last2SixMonths;
    protected boolean thisFinancialYear;
    protected boolean lastFinancialYear;
    protected boolean last3Months;
    protected boolean last12Months;
    protected boolean last6BiMonths;
    protected boolean last5FinancialYears;
    protected boolean lastWeek;
    protected boolean reportingBiWeek;
    protected boolean lastBiWeek;
    protected boolean last4Weeks;
    protected boolean last4BiWeeks;
    protected boolean last12Weeks;
    protected boolean last52Weeks;
    protected boolean yesterday;


    public void setReportingMonth( boolean reportingMonth )
    {
        this.reportingMonth = reportingMonth;
    }

    public void setReportingBimonth( boolean reportingBimonth )
    {
        this.reportingBimonth = reportingBimonth;
    }

    public void setLastBiMonth( boolean lastBiMonth )
    {
        this.lastBiMonth = lastBiMonth;
    }

    public void setReportingQuarter( boolean reportingQuarter )
    {
        this.reportingQuarter = reportingQuarter;
    }

    public void setLast6Months( boolean last6Months )
    {
        this.last6Months = last6Months;
    }

    public void setReportingSixMonth( boolean reportingSixMonth )
    {
        this.reportingSixMonth = reportingSixMonth;
    }

    public void setWeeksThisYear( boolean weeksThisYear )
    {
        this.weeksThisYear = weeksThisYear;
    }

    public void setMonthsThisYear( boolean monthsThisYear )
    {
        this.monthsThisYear = monthsThisYear;
    }

    public void setBiMonthsThisYear( boolean biMonthsThisYear )
    {
        this.biMonthsThisYear = biMonthsThisYear;
    }

    public void setQuartersThisYear( boolean quartersThisYear )
    {
        this.quartersThisYear = quartersThisYear;
    }

    public void setThisYear( boolean thisYear )
    {
        this.thisYear = thisYear;
    }

    public void setMonthsLastYear( boolean monthsLastYear )
    {
        this.monthsLastYear = monthsLastYear;
    }

    public void setQuartersLastYear( boolean quartersLastYear )
    {
        this.quartersLastYear = quartersLastYear;
    }

    public void setLast5Years( boolean last5Years )
    {
        this.last5Years = last5Years;
    }

    public void setLastYear( boolean lastYear )
    {
        this.lastYear = lastYear;
    }

    public void setLast12Months( boolean last12Months )
    {
        this.last12Months = last12Months;
    }

    public void setLast4Quarters( boolean last4Quarters )
    {
        this.last4Quarters = last4Quarters;
    }

    public void setLast2SixMonths( boolean last2SixMonths )
    {
        this.last2SixMonths = last2SixMonths;
    }

    public void setThisFinancialYear( boolean thisFinancialYear )
    {
        this.thisFinancialYear = thisFinancialYear;
    }

    public void setLastFinancialYear( boolean lastFinancialYear )
    {
        this.lastFinancialYear = lastFinancialYear;
    }

    public void setLast3Months( boolean last3Months )
    {
        this.last3Months = last3Months;
    }

    public void setLast6BiMonths( boolean last6BiMonths )
    {
        this.last6BiMonths = last6BiMonths;
    }

    public void setLast5FinancialYears( boolean last5FinancialYears )
    {
        this.last5FinancialYears = last5FinancialYears;
    }

    public void setLastWeek( boolean lastWeek )
    {
        this.lastWeek = lastWeek;
    }

    public void setLast4Weeks( boolean last4Weeks )
    {
        this.last4Weeks = last4Weeks;
    }

    public void setLast12Weeks( boolean last12Weeks )
    {
        this.last12Weeks = last12Weeks;
    }

    public void setLast52Weeks( boolean last52Weeks )
    {
        this.last52Weeks = last52Weeks;
    }

    public void setReportingDay ( boolean reportingDay )
    {
        this.reportingDay = reportingDay;
    }

    public void setYesterday( boolean yesterday )
    {
        this.yesterday = yesterday;
    }

    public void setLast3Days( boolean last3Days )
    {
        this.last3Days = last3Days;
    }

    public void setLast7Days( boolean last7Days )
    {
        this.last7Days = last7Days;
    }

    public void setLast14Days( boolean last14Days )
    {
        this.last14Days = last14Days;
    }

    public void setLastMonth( boolean lastMonth )
    {
        this.lastMonth = lastMonth;
    }

    public void setReportingWeek( boolean reportingWeek )
    {
        this.reportingWeek = reportingWeek;
    }

    public void setLastQuarter( boolean lastQuarter )
    {
        this.lastQuarter = lastQuarter;
    }

    public void setLastSixMonth( boolean lastSixMonth )
    {
        this.lastSixMonth = lastSixMonth;
    }
}
