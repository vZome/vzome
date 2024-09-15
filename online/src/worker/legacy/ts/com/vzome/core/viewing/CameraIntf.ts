/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    export interface CameraIntf {
        isPerspective(): boolean;

        getFieldOfView(): number;

        getViewDistance(): number;

        getMagnification(): number;

        getLookAtPointRV(): com.vzome.core.math.RealVector;

        getLookDirectionRV(): com.vzome.core.math.RealVector;

        getUpDirectionRV(): com.vzome.core.math.RealVector;

        mapViewToWorld(rv: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector;
    }
}

