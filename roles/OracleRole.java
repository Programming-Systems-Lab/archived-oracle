/*
 * Title: OracleRole
 * Description:  Defines Oracle role for groupspace.
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */


package psl.oracle.roles;
import psl.groupspace.*;
import psl.util.*;

public interface OracleRole
{
    public void oracleEvent(GroupspaceEvent ge);

}
