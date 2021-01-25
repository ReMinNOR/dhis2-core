package org.hisp.dhis.webapi.webdomain.sharing.comparator;



import java.util.Comparator;

import org.hisp.dhis.webapi.webdomain.sharing.SharingUserGroupAccess;

public class SharingUserGroupAccessNameComparator
    implements Comparator<SharingUserGroupAccess>
{
    public static final SharingUserGroupAccessNameComparator INSTANCE = new SharingUserGroupAccessNameComparator();

    @Override
    public int compare( SharingUserGroupAccess s1, SharingUserGroupAccess s2 )
    {
        return s1 != null && s1.getName() != null ? s2 != null && s2.getName() != null ? 
            s1.getName().compareToIgnoreCase( s2.getName() ) : -1 : 1;
    }
}
