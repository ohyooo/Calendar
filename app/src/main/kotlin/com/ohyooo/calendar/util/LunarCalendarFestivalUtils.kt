package com.ohyooo.calendar.util

import androidx.collection.SparseArrayCompat
import androidx.collection.set
import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

class LunarDate {
    /**
     * 获取查询日期的年份生肖
     * @return
     */
    //生肖年
    var animal = ""

    /**
     * 获取查询日期年份的天干地支
     * @return
     */
    //干支年
    var ganZhiYear = ""

    /**
     * 获取查询日期的农历年份
     * @return
     */
    //阴历年
    var lunarYear = ""

    /**
     * 获取查询日期的农历月份
     * @return
     */
    //阴历月
    var lunarMonth = ""

    /**
     * 获取查询日期的农历日
     * @return
     */
    //阴历日
    var lunarDay = ""

    /**
     * 获取查询日期的公历节日（不是节日返回空）
     * @return
     */
    //阳历节日
    var solarFestival = ""

    /**
     * 获取查询日期的农历节日（不是节日返回空）
     * @return
     */
    //阴历节日
    var lunarFestival = ""

    /**
     * 获取查询日期的节气数据（不是节气返回空）
     * @return
     */
    //节气
    var lunarTerm = ""
}

/**
 * 获取输入公历日期的生肖、天干地支、农历年、农历月、农历日、公历节日、农历节日、24节气等数据
 * DATE 2020.08.13
 */
object LunarCalendarFestivalUtils {

    private val lunarInfo = longArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0
    )

    //阳历天数
    private val solarMonths = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    //生肖
    private val animals = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")

    //天干
    private val tGan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")

    //地支
    private val dZhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    //二十四节气
    private val solarTerms = arrayOf("小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至")

    //二十四节气日期偏移度
    private const val D = 0.2422

    //特殊年份节气日期偏移
    private val INCREASE_OFFSET_MAP = SparseArrayCompat<IntArray>() // +1 偏移
    private val DECREASE_OFFSET_MAP = SparseArrayCompat<IntArray>() // -1 偏移

    //定义一个二维数组，第一维数组存储的是20世纪的节气C值，第二维数组存储的是21世纪的节气C值,0到23个，依次代表立春、雨水...大寒节气的C值
    private val CENTURY_ARRAY = arrayOf(doubleArrayOf(6.11, 20.84, 4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.888, 6.318, 21.86, 6.5, 22.2, 7.928, 23.65, 8.35, 23.95, 8.44, 23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6), doubleArrayOf(5.4055, 20.12, 3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678, 21.37, 7.108, 22.83, 7.5, 23.13, 7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94))

    //农历月份
    private val lunarNumber = arrayOf("一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二")

    //农历年
    private val lunarYears = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")
    private val chineseTen = arrayOf("初", "十", "廿", "卅")

    //农历节日
    private val lunarHoliday = arrayOf("0101 春节", "0115 元宵节", "0202 龙头节", "0505 端午节", "0707 七夕节", "0715 中元节", "0815 中秋节", "0909 重阳节", "1001 寒衣节", "1015 下元节", "1208 腊八节", "1223 小年")

    //公立节日
    private val solarHoliday = arrayOf(
        "0101 元旦", "0214 情人节", "0308 妇女节", "0312 植树节", "0315 消费者权益日", "0401 愚人节", "0422 地球日", "0423 读书日", "0501 劳动节", "0504 青年节", "0512 护士节", "0518 博物馆日", "0519 旅游日", "0601 儿童节",
        "0701 建党节", "0801 建军节", "0910 教师节", "1001 国庆节", "1024 联合国日", "1204 宪法日", "1224 平安夜", "1225 圣诞节"
    )

    // /**
    //  * 测试方法
    //  * @param args
    //  */
    // @JvmStatic
    // fun main(args: Array<String>) {
    //     val festival = LunarCalendarFestivalUtils()
    //     festival.initLunarCalendarInfo("2021-06-25")
    //     println("农历" + festival.lunarYear + "年" + festival.lunarMonth + "月" + festival.lunarDay + "日")
    //     println(festival.ganZhiYear + "【" + festival.animal + "】年")
    //     println(festival.lunarTerm)
    //     println(festival.solarFestival)
    //     println(festival.lunarFestival)
    // }

    init {
        DECREASE_OFFSET_MAP[0] = intArrayOf(2019) //小寒
        INCREASE_OFFSET_MAP[1] = intArrayOf(2082) //大寒
        DECREASE_OFFSET_MAP[3] = intArrayOf(2026) //雨水
        INCREASE_OFFSET_MAP[5] = intArrayOf(2084) //春分
        INCREASE_OFFSET_MAP[9] = intArrayOf(2008) //小满
        INCREASE_OFFSET_MAP[10] = intArrayOf(1902) //芒种
        INCREASE_OFFSET_MAP[11] = intArrayOf(1928) //夏至
        INCREASE_OFFSET_MAP[12] = intArrayOf(1925, 2016) //小暑
        INCREASE_OFFSET_MAP[13] = intArrayOf(1922) //大暑
        INCREASE_OFFSET_MAP[14] = intArrayOf(2002) //立秋
        INCREASE_OFFSET_MAP[16] = intArrayOf(1927) //白露
        INCREASE_OFFSET_MAP[17] = intArrayOf(1942) //秋分
        INCREASE_OFFSET_MAP[19] = intArrayOf(2089) //霜降
        INCREASE_OFFSET_MAP[20] = intArrayOf(2089) //立冬
        INCREASE_OFFSET_MAP[21] = intArrayOf(1978) //小雪
        INCREASE_OFFSET_MAP[22] = intArrayOf(1954) //大雪
        DECREASE_OFFSET_MAP[23] = intArrayOf(1918, 2021) //冬至
    }

    /**
     * 返回农历y年的总天数
     * @param y
     * @return
     */
    private fun lunarYearDays(y: Int): Int {
        var i: Int
        var sum = 348
        i = 0x8000
        while (i > 0x8) {
            sum += if (lunarInfo[y - 1900] and i.toLong() != 0L) 1 else 0
            i = i shr 1
        }
        return sum + leapDays(y)
    }

    /**
     * 返回农历y年闰月的天数
     */
    private fun leapDays(y: Int) = if (leapMonth(y) != 0) {
        if (lunarInfo[y - 1900] and 0x10000 != 0L) 30 else 29
    } else 0

    /**
     * 判断y年的农历中那个月是闰月,不是闰月返回0
     * @param y
     * @return
     */
    private fun leapMonth(y: Int) = (lunarInfo[y - 1900] and 0xf).toInt()

    /**
     * 返回农历y年m月的总天数
     * @param y
     * @param m
     * @return
     */
    private fun monthDays(y: Int, m: Int) = if (lunarInfo[y - 1900] and (0x10000 shr m).toLong() != 0L) 30 else 29

    /**
     * 获取阴历年
     * @param year
     * @return
     */
    private fun getLunarYearString(year: String): String {
        val y1 = (year[0].toString() + "").toInt()
        val y2 = (year[1].toString() + "").toInt()
        val y3 = (year[2].toString() + "").toInt()
        val y4 = (year[3].toString() + "").toInt()
        return lunarYears[y1] + lunarYears[y2] + lunarYears[y3] + lunarYears[y4]
    }

    /**
     * 获取阴历日
     */
    private fun getLunarDayString(day: Int): String {
        val n = if (day % 10 == 0) 9 else day % 10 - 1
        if (day > 30) return ""
        return if (day == 10) "初十" else chineseTen[day / 10] + lunarNumber[n]
    }

    /**
     * 特例,特殊的年分的节气偏移量,由于公式并不完善，所以算出的个别节气的第几天数并不准确，在此返回其偏移量
     * @param year 年份
     * @param n 节气编号
     * @return 返回其偏移量
     */
    private fun specialYearOffset(year: Int, n: Int): Int {
        var offset = 0
        offset += getOffset(DECREASE_OFFSET_MAP, year, n, -1)
        offset += getOffset(INCREASE_OFFSET_MAP, year, n, 1)
        return offset
    }

    /**
     * 节气偏移量计算
     * @param map
     * @param year
     * @param n
     * @param offset
     * @return
     */
    private fun getOffset(map: SparseArrayCompat<IntArray>, year: Int, n: Int, offset: Int): Int {
        var off = 0
        val years = map[n]
        if (null != years) {
            for (i in years) {
                if (i == year) {
                    off = offset
                    break
                }
            }
        }
        return off
    }

    /**
     * 获取某年的第n个节气为几日(从0小寒起算)
     * @param year
     * @param n
     * @return
     */
    private fun sTerm(year: Int, n: Int): Int {
        val centuryValue: Double //节气的世纪值，每个节气的每个世纪值都不同
        val centuryIndex = when (year) {
            in 1901..2000 -> 0 //20世纪
            in 2001..2100 -> 1 //21世纪
            else -> return Int.MIN_VALUE // throw RuntimeException("不支持此年份：$year，目前只支持1901年到2100年的时间范围")
        }
        centuryValue = CENTURY_ARRAY[centuryIndex][n]
        var y = year % 100 //步骤1:取年分的后两位数
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) { //闰年
            if (n == 0 || n == 1 || n == 2 || n == 3) {
                //注意：凡闰年3月1日前闰年数要减一，即：L=[(Y-1)/4],因为小寒、大寒、立春、雨水这两个节气都小于3月1日,所以 y = y-1
                y -= 1 //步骤2
            }
        }
        var dateNum = (y * D + centuryValue).toInt() - (y / 4) //步骤3，使用公式[Y*D+C]-L计算
        dateNum += specialYearOffset(year, n) //步骤4，加上特殊的年分的节气偏移量
        return dateNum
    }

    /**
     * 母亲节和父亲节
     * @param year
     * @param month
     * @param day
     * @return
     */
    private fun getMotherOrFatherDay(year: Int, month: Int, day: Int): String? {
        if (month != 5 && month != 6) return null
        if (month == 5 && (day < 8 || day > 14) || month == 6 && (day < 15 || day > 21)) return null
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        var weekDate = calendar[Calendar.DAY_OF_WEEK]
        weekDate = if (weekDate == 1) 7 else weekDate - 1
        when (month) {
            5 -> if (day == 15 - weekDate) return "母亲节"
            6 -> if (day == 22 - weekDate) return "父亲节"
        }
        return null
    }

    /**
     * 感恩节
     * @param year
     * @param month
     * @param day
     * @return
     */
    private fun thanksgiving(year: Int, month: Int, day: Int): String? {
        if (month != 11) return null
        if (day < 19 || day > 28) return null
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        var weekDate = calendar[Calendar.DAY_OF_WEEK]
        weekDate = if (weekDate == 1) 7 else weekDate - 1
        return if (day == 29 - weekDate + 4) "感恩节" else null
    }

    /**
     * 获取复活节
     * @param year
     * @param month
     * @param day
     * @return
     */
    private fun getEasterDay(year: Int, month: Int, day: Int): String? {
        val n = year - 1900
        val a = n % 19
        val q = n / 4
        val b = (7 * a + 1) / 19
        val m = (11 * a + 4 - b) % 29
        val w = (n + q + 31 - m) % 7
        val answer = 25 - m - w
        val easterDay = (if (answer > 0) "$year-4-$answer" else year.toString() + "-" + 3 + "-" + (31 + answer))
        val searchDay = "$year-$month-$day"
        return if (searchDay == easterDay) "复活节" else null
    }

    /**
     * 输入公历日期初始化当前日期的生肖、天干地支、农历年、农历月、农历日、公历节日、农历节日、24节气
     * 输入日期的格式为(YYYY-MM-DD)
     * @param currentDate
     */
    fun initLunarCalendarInfo(currentDate: LocalDate, ld: LunarDate) {
        //设置生肖
        val year = currentDate.year
        ld.animal = animals[(year - 4) % 12]
        //设置天干地支
        val num = year - 1900 + 36
        ld.ganZhiYear = tGan[num % 10] + dZhi[num % 12]
        ///////////设置阴历/////////////////////////////////////////////////////////

        // 获取当前日期与1900年1月31日相差的天数
        var offset = DAYS.between(baseDate, currentDate).toInt()

        //用offset减去每农历年的天数，计算当天是农历第几天 iYear最终结果是农历的年份
        var daysOfYear = 0
        var iYear = 1900
        while (iYear < 10000 && offset > 0) {
            daysOfYear = lunarYearDays(iYear)
            offset -= daysOfYear
            iYear++
        }
        if (offset < 0) {
            offset += daysOfYear
            iYear--
        }
        ld.lunarYear = getLunarYearString(iYear.toString() + "")
        val leapMonth = leapMonth(iYear) // 闰哪个月, 1-12
        var leap = false

        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        var daysOfMonth = 0
        var iMonth = 1
        while (iMonth < 13 && offset > 0) {

            // 闰月
            if (leapMonth > 0 && iMonth == leapMonth + 1 && !leap) {
                --iMonth
                leap = true
                daysOfMonth = leapDays(iYear)
            } else daysOfMonth = monthDays(iYear, iMonth)
            offset -= daysOfMonth
            // 解除闰月
            if (leap && iMonth == leapMonth + 1) leap = false
            iMonth++
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false
            } else {
                leap = true
                --iMonth
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth
            --iMonth
        }
        // 设置对应的阴历月份
        ld.lunarMonth = when {
            leap -> "闰${lunarNumber[iMonth - 1]}"
            iMonth == 1 -> "正"
            iMonth == 12 -> "腊"
            else -> lunarNumber[iMonth - 1]
        }

        //设置阴历日
        val iDay = offset + 1
        ld.lunarDay = getLunarDayString(iDay)

        //设置节气
        val month = currentDate.monthValue
        val day = currentDate.dayOfMonth
        ld.lunarTerm = when (day) {
            sTerm(year, (month - 1) * 2) -> solarTerms[(month - 1) * 2]
            sTerm(year, (month - 1) * 2 + 1) -> solarTerms[(month - 1) * 2 + 1]
            else -> ""
        }

        //设置阳历节日
        var solarFestival = ""
        for (s in solarHoliday) {
            // 返回公历节假日名称
            val sd = s.split(" ").toTypedArray()[0] // 节假日的日期
            val sdv = s.split(" ").toTypedArray()[1] // 节假日的名称
            val smonth_v = month
            val sday_v = day
            val smd = smonth_v + sday_v
            if (sd.trim() == "$smd") {
                solarFestival = sdv
                break
            }
        }
        //判断节日是否是父亲节或母亲节
        val motherOrFatherDay = getMotherOrFatherDay(year, month, day)
        if (motherOrFatherDay != null) {
            solarFestival = motherOrFatherDay
        }
        //判断节日是否是复活节
        val easterDay = getEasterDay(year, month, day)
        if (easterDay != null) {
            solarFestival = easterDay
        }
        //判断节日是否是感恩节
        val thanksgiving = thanksgiving(year, month, day)
        if (thanksgiving != null) {
            solarFestival = thanksgiving
        }

        ld.solarFestival = solarFestival

        //设置阴历节日
        var lunarFestival = ""
        for (s in lunarHoliday) {
            //阴历闰月节日
            if (leap) {
                break
            }
            // 返回农历节假日名称
            val ld = s.split(" ").toTypedArray()[0] // 节假日的日期
            val ldv = s.split(" ").toTypedArray()[1] // 节假日的名称
            val lmonth_v = if (iMonth < 10) "0$iMonth" else iMonth.toString() + ""
            val lday_v = if (iDay < 10) "0$iDay" else iDay.toString() + ""
            val lmd = lmonth_v + lday_v
            if ("12" == lmonth_v) { // 除夕夜需要特殊处理
                if (daysOfMonth == 29 && iDay == 29 || daysOfMonth == 30 && iDay == 30) {
                    lunarFestival = "除夕"
                    break
                }
            }
            if (ld.trim() == lmd.trim()) {
                lunarFestival = ldv
                break
            }
        }
        if ("清明" == ld.lunarTerm) {
            lunarFestival = "清明节"
        }
        ld.lunarFestival = lunarFestival
    }
}