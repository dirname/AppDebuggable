package cn.forgiveher.appdebuggable.Apps

import java.util.Comparator

class PinyinComparator : Comparator<SortModel> {

    override fun compare(o1: SortModel, o2: SortModel): Int {
        return if (o1.letters == "@" || o2.letters == "#") {
            -1
        } else if (o1.letters == "#" || o2.letters == "@") {
            1
        } else {
            o1.letters!!.compareTo(o2.letters!!)
        }
    }

}
