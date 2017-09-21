package eap.sky.util.plane;

import eap.sky.util.*;

import java.awt.geom.*;
import java.util.*;

/*****************************************************************
*
*****************************************************************/
public class ReadOnlyDitherPattern extends DitherPattern {


/*****************************************************************
* Create an empty dither pattern.
*****************************************************************/
public ReadOnlyDitherPattern(DitherPattern dither) {

    super(Collections.unmodifiableList(dither.points));

} // end of empty constructor

} // end of DitherPattern class
