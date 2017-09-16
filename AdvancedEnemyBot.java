package ms;
import robocode.*;

public class AdvancedEnemyBot extends EnemyBot {
    private double x;
    private double y;

    AdvancedEnemyBot() {
        reset();
    }

    public double getX() {
        return x;
    }
    
    public double getY() { 
        return y;
    }

    public void reset() {
        super.reset();
        x = 0.0;
        y = 0.0;
    }

    public void update(ScannedRobotEvent event, Robot robot) {
        super.update(event);
        double absBearingDeg = (robot.getHeading() + event.getBearing());
        absBearingDeg = (absBearingDeg < 0) ? absBearingDeg += 360 : absBearingDeg;
        x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * event.getDistance();
        y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * event.getDistance();
    }

    public double getFutureX(long when) {
        return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
    }

    public double getFutureY(long when) {
        return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
    }
}
