package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MojoPojo {

    public int i;
    public Integer io;
    private long l;
    private Long lo;
    public String s;
    public boolean b;
    public Boolean bo;
    public Date d;
    public LocalTime lt;
    public LocalDateTime ldt;
    public MojoPojo mp;
    public List<String> ls;
    public Map<Long, String> mls;
    public int[] ia;

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public Long getLo() {
        return lo;
    }

    public void setLo(Long lo) {
        this.lo = lo;
    }

    @Override
    public String toString() {
        return "MojoPojo [i=" + i + ", io=" + io + ", l=" + l + ", lo=" + lo + ", s=" + s + ", b=" + b + ", bo=" + bo + ", d=" + d + ", lt=" + lt + ", ldt="
                        + ldt + ", mp=" + mp + ", ls=" + ls + ", mls=" + mls + ", ia=" + Arrays.toString(ia) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (b ? 1231 : 1237);
        result = prime * result + ((bo == null) ? 0 : bo.hashCode());
        result = prime * result + ((d == null) ? 0 : d.hashCode());
        result = prime * result + i;
        result = prime * result + Arrays.hashCode(ia);
        result = prime * result + ((io == null) ? 0 : io.hashCode());
        result = prime * result + (int) (l ^ (l >>> 32));
        result = prime * result + ((ldt == null) ? 0 : ldt.hashCode());
        result = prime * result + ((lo == null) ? 0 : lo.hashCode());
        result = prime * result + ((ls == null) ? 0 : ls.hashCode());
        result = prime * result + ((lt == null) ? 0 : lt.hashCode());
        result = prime * result + ((mls == null) ? 0 : mls.hashCode());
        result = prime * result + ((mp == null) ? 0 : mp.hashCode());
        result = prime * result + ((s == null) ? 0 : s.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MojoPojo other = (MojoPojo) obj;
        if (b != other.b)
            return false;
        if (bo == null) {
            if (other.bo != null)
                return false;
        } else if (!bo.equals(other.bo))
            return false;
        if (d == null) {
            if (other.d != null)
                return false;
        } else if (!d.equals(other.d))
            return false;
        if (i != other.i)
            return false;
        if (!Arrays.equals(ia, other.ia))
            return false;
        if (io == null) {
            if (other.io != null)
                return false;
        } else if (!io.equals(other.io))
            return false;
        if (l != other.l)
            return false;
        if (ldt == null) {
            if (other.ldt != null)
                return false;
        } else if (!ldt.equals(other.ldt))
            return false;
        if (lo == null) {
            if (other.lo != null)
                return false;
        } else if (!lo.equals(other.lo))
            return false;
        if (ls == null) {
            if (other.ls != null)
                return false;
        } else if (!ls.equals(other.ls))
            return false;
        if (lt == null) {
            if (other.lt != null)
                return false;
        } else if (!lt.equals(other.lt))
            return false;
        if (mls == null) {
            if (other.mls != null)
                return false;
        } else if (!mls.equals(other.mls))
            return false;
        if (mp == null) {
            if (other.mp != null)
                return false;
        } else if (!mp.equals(other.mp))
            return false;
        if (s == null) {
            if (other.s != null)
                return false;
        } else if (!s.equals(other.s))
            return false;
        return true;
    }

}
