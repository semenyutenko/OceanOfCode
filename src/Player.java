import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static final int CHANCE_SHOT = 70;
    private static final int SONAR_TRIGGER = 25;
    private static final int STRATEGY_TUMBLER = 0;
    private static final int LEVEL_ROUTE = 100;

    private static String[] field;
    private static ArrayList<Point> points = new ArrayList();
    private static Point myPoint;
    private static Point targetPoint;
    private static Point lastShot;
    private static int oppTestLife = 6;
    private static int lastSonar = 0;
    private static boolean wasShot = false;
    private static int torpCharge;
    private static int health;
    private static HashSet<Point> routeSet = new HashSet<Point>();
    private static String enemyOrder;



    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        fieldCreate(height, in);
        pointsCreate();
        myPointCreate();

        System.out.println("" + myPoint.getX() + " " + myPoint.getY());

        // game loop
        while (true) {
            int X = in.nextInt();
            int Y = in.nextInt();
            int myLife = in.nextInt();
            health = myLife;
            int oppLife = in.nextInt();
            int torpedoCooldown = in.nextInt();
            torpCharge = torpedoCooldown;
            int sonarCooldown = in.nextInt();
            int silenceCooldown = in.nextInt();
            int mineCooldown = in.nextInt();
            String sonarResult = in.next();

            if (in.hasNextLine()) {
                in.nextLine();
            }
            String opponentOrders = in.nextLine();
            enemyOrder = opponentOrders;

            int wasSurface = (opponentOrders.contains("SURFACE")) ? 1 : 0;
            String[] separatedOppOrders = opponentOrders.split("\\|");



            sonarHandle(sonarResult);
            myShotHandle(oppLife, opponentOrders, wasSurface);

            for (String sepOrder: separatedOppOrders){
                surfaceHandle(sepOrder);
                moveHandle(sepOrder);
                torpedoHandle(sepOrder, oppLife);
                silenceHandle(sepOrder);
            }

            String action;
            action = actionStandart(sonarCooldown, silenceCooldown);
            System.out.println(action);

            wasShot = (action.startsWith("TORPEDO") || action.contains("| TORPEDO")) ? true : false;
            oppTestLife = oppLife;
        }
    }

    protected static void fieldCreate(int height, Scanner in) {
        field = new String[height];
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            field[i] = line;
        }
    }

    protected static void myPointCreate(){
        int nSector = getSonar();

        while (true){
            Point startPoint = points.get((int)(Math.random() * points.size()));
            if (startPoint.isSector(nSector)){
                myPoint = new Point(startPoint.getX(), startPoint.getY());
                break;
            }
        }
    }

    protected static void pointsCreate(){
        for (int i = 0; i < 15; i++){
            for (int j = 0; j < 15; j++){
                if (field[i].charAt(j) == '.') points.add(new Point(j, i));
            }
        }

    }

    protected static void pointsCreate(int x, int y){
        int x1, x2, y1, y2;
        if (x / 7 == 0){
            x1 = 0;
            x2 = 7;
        }else {
            x1 = 7;
            x2 = 14;
        }
        if (y / 7 == 0){
            y1 = 0;
            y2 = 7;
        }else {
            y1 = 7;
            y2 = 14;
        }
        for (int i = x1; i <= x2; i++){
            for (int j = y1; j <= y2; j++){
                if (field[i].charAt(j) == '.') points.add(new Point(j, i));
            }
        }
    }


    protected static void sonarHandle(String sonarResult){
        if(sonarResult.equals("Y")){
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (!nextPoint.isSector(lastSonar)) {
                    pointIterator.remove();
                }
            }
        }
        if(sonarResult.equals("N")){
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (nextPoint.isSector(lastSonar)) {
                    pointIterator.remove();
                }
            }
        }
    }

    protected static void myShotHandle(int oppLife, String opponentOrders, int wasSurface){
        if(lastShot != null && wasShot && oppLife == oppTestLife){
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (nextPoint.isNearby(lastShot.getX(), lastShot.getY())) {
                    pointIterator.remove();
                }
            }
        }
        if(lastShot != null && wasShot &&
                !opponentOrders.contains("TORPEDO") && oppTestLife - oppLife == 1 + wasSurface){
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (!nextPoint.isNearby(lastShot.getX(), lastShot.getY()) ||
                        (nextPoint.getX() == lastShot.getX() && nextPoint.getY() == lastShot.getY())) {
                    pointIterator.remove();
                }
            }
        }
        if(lastShot != null && wasShot &&
                !opponentOrders.contains("TORPEDO") && oppTestLife - oppLife == 2 + wasSurface){
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (nextPoint.getX() != lastShot.getX() || nextPoint.getY() != lastShot.getY()) {
                    pointIterator.remove();
                }
            }
        }
    }

    protected static Point getTargetPoint(){
        return points.get(0);
    }

    protected static void surfaceHandle(String sepOrder){
        if (sepOrder.contains("SURFACE")) {
            int indexSurface = sepOrder.indexOf("SURFACE");
            String surface = sepOrder.substring(indexSurface);
            int sector = Integer.parseInt(surface.substring(8, 9));
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (!nextPoint.isSector(sector)) {
                    pointIterator.remove();
                }
            }
            oppTestLife -= 1;
        }
    }

    protected static void moveHandle(String sepOrder){
        if (sepOrder.contains("MOVE")) {
            int indexMove = sepOrder.indexOf("MOVE");
            String move = sepOrder.substring(indexMove);
            char direction = move.charAt(5);
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (!nextPoint.checkMove(direction)) {
                    pointIterator.remove();
                }
            }
        }
    }
    protected static void triggerHandle(String sepOrder, int oppLife){
        if (sepOrder.contains("TRIGGER")) {
            int indexTrigger = sepOrder.indexOf("TRIGGER");
            String trigger = sepOrder.substring(indexTrigger);
            String[] words = trigger.split(" ");
            int trigX = Integer.parseInt(words[1]);
            int trigY = Integer.parseInt(words[2]);

            if (oppLife == oppTestLife){
                Iterator<Point> pointIterator = points.iterator();
                while (pointIterator.hasNext()) {
                    Point nextPoint = pointIterator.next();
                    if (!nextPoint.isDistShot(trigX, trigY) || nextPoint.isNearby(trigX, trigY)) {
                        pointIterator.remove();
                    }
                }
            }

            if (!wasShot && oppTestLife - oppLife == 1 && !enemyOrder.contains("TORPEDO")){
                Iterator<Point> pointIterator = points.iterator();
                while (pointIterator.hasNext()) {
                    Point nextPoint = pointIterator.next();
                    if (!nextPoint.isNearby(trigX, trigY) || (nextPoint.getX() == trigX && nextPoint.getY() == trigY)){
                        pointIterator.remove();
                    }
                }
            }

            if (!wasShot && oppTestLife - oppLife == 2 && !enemyOrder.contains("TORPEDO")){
                Iterator<Point> pointIterator = points.iterator();
                while (pointIterator.hasNext()) {
                    Point nextPoint = pointIterator.next();
                    if (!(nextPoint.getX() == trigX && nextPoint.getY() == trigY)){
                        pointIterator.remove();
                    }
                }
            }
        }

    }
    protected static void torpedoHandle(String sepOrder, int oppLife){
        if (sepOrder.contains("TORPEDO")) {
            int indexTorpedo = sepOrder.indexOf("TORPEDO");
            String torpedo = sepOrder.substring(indexTorpedo);
            String[] words = torpedo.split(" ");
            int shotX = Integer.parseInt(words[1]);
            int shotY = Integer.parseInt(words[2]);

            if (oppLife == oppTestLife){
                Iterator<Point> pointIterator = points.iterator();
                while (pointIterator.hasNext()) {
                    Point nextPoint = pointIterator.next();
                    if (!nextPoint.isDistShot(shotX, shotY) || nextPoint.isNearby(shotX, shotY)) {
                        pointIterator.remove();
                    }
                }
            }

            if (!wasShot && oppTestLife - oppLife == 1 && !enemyOrder.contains("TRIGGER")){
                Iterator<Point> pointIterator = points.iterator();
                while (pointIterator.hasNext()) {
                    Point nextPoint = pointIterator.next();
                    if (!nextPoint.isNearby(shotX, shotY) || (nextPoint.getX() == shotX && nextPoint.getY() == shotY)){
                        pointIterator.remove();
                    }
                }
            }

            if (!wasShot && oppTestLife - oppLife == 2 && !enemyOrder.contains("TRIGGER")){
                Iterator<Point> pointIterator = points.iterator();
                while (pointIterator.hasNext()) {
                    Point nextPoint = pointIterator.next();
                    if (!(nextPoint.getX() == shotX && nextPoint.getY() == shotY)){
                        pointIterator.remove();
                    }
                }
            }
        }

    }
    protected static void silenceHandle(String sepOrder){
        if (sepOrder.contains("SILENCE")) {
            ListIterator<Point> pointIterator = points.listIterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                ArrayList<Point> newPoints = nextPoint.silenceOptions();
                for(Point point: newPoints){
                    pointIterator.add(point);
                }
            }
        }
    }

    protected static boolean isReadyShot(Point targetPoint){
        return (myPoint.isDistShot(targetPoint.getX(), targetPoint.getY()))
                &&points.size() <= CHANCE_SHOT && torpCharge == 0 && !myPoint.isTheSame(targetPoint);
    }
    private static boolean isThereObstacle(Point start, Point finish){
        ArrayList<Point> startList = start.getDiamond(2);
        startList.addAll(start.getDiamond(1));
        ArrayList<Point> finisList = start.getDiamond(2);
        finisList.addAll(finish.getDiamond(1));
        ArrayList<Point> mainList = new ArrayList<>();
        for(Point point: startList){
            if(finisList.contains(point))mainList.add(point);
        }

        return false;
    }

    protected static int getSonar(){
        int max = 0;
        int[] sectorCapacity = new int[9];
        for(Point point: points){
            int n = (point.getX() / 5 + 1) + (point.getY() / 5 * 3 );
            sectorCapacity[n - 1] = sectorCapacity[n - 1] + 1;
        }
        for(int i = 0; i < 9; i++){
            if (max < sectorCapacity[i]){
                max = sectorCapacity[i];
                lastSonar = i + 1;
            }
        }
        return lastSonar;
    }
    protected static String actionStandart(int sonarCooldown, int silenceCooldown) {
        StringBuilder myOrder = new StringBuilder();
        targetPoint = getTargetPoint();
        ;

        if (isReadyShot(targetPoint)) {
            lastShot = targetPoint;
            torpCharge = 3;
            myOrder.append("TORPEDO " + targetPoint.getX() + " " + targetPoint.getY() + " | ");
        }

        myOrder.append(myPoint.getMove());
        if (myOrder.toString().contains("SURFACE")) {
            myOrder.append(" | " + myPoint.getMove());
        }
        if (torpCharge > 0) {
            torpCharge--;
            myOrder.append(" TORPEDO");
        } else if (sonarCooldown <= 4 && sonarCooldown > 0 && points.size() > SONAR_TRIGGER) myOrder.append(" SONAR");
        else if (silenceCooldown <= 6 && silenceCooldown > 0) myOrder.append(" SILENCE");

        targetPoint = getTargetPoint();
        ;

        if (isReadyShot(targetPoint)) {
            lastShot = targetPoint;
            torpCharge = 3;
            myOrder.append(" | TORPEDO " + targetPoint.getX() + " " + targetPoint.getY());
        }
        if (points.size() > SONAR_TRIGGER && sonarCooldown == 0) myOrder.append(" | SONAR " + getSonar());
        if (silenceCooldown == 0) myOrder.append(" | " + myPoint.getSilence());

        return myOrder.toString();
    }

    public static class Point{

        private int x;
        private int y;
        private HashSet<int[]> route = new HashSet<int[]>();

        Point(int x, int y){
            this.x = x;
            this.y = y;
            route.add(new int[]{x, y});
        }

        private int getX(){
            return x;
        }

        private int getY(){
            return y;
        }

        private boolean checkMove(char direction){
            switch (direction){
                case 'N': y--;
                    break;
                case 'S': y++;
                    break;
                case 'W': x--;
                    break;
                case 'E': x++;
                    break;
                default: break;
            }
            if (isOnField()) {
                route.add(new int[]{x, y});
                return true;
            }
            return false;
        }

        private boolean isSector(int sector){
            if ((sector - 1) % 3 * 5 <= x && ((sector - 1)  % 3 + 1) * 5 > x &&
                    (sector - 1) / 3 * 5 <= y && ((sector - 1) / 3 + 1) * 5 > y){
                return true;
            }
            return false;
        }




        protected boolean isDistShot(int targetX, int targetY){
            if (x == targetX && y == targetY) return false;
            Point target = new Point(targetX, targetY);
            ArrayList<Point> thisNeighbours = new ArrayList<>();
            ArrayList<Point> targetNeighbours = new ArrayList<>();
            thisNeighbours = getDiamond(2);
            thisNeighbours.addAll(getDiamond(1));
            targetNeighbours = target.getDiamond(2);
            targetNeighbours.addAll(target.getDiamond(1));
            for(Point point : thisNeighbours){
                if (targetNeighbours.contains(point) && point.isOnField())return true;
            }
            return false;  //if ((Math.abs(x - targetX) + Math.abs(y - targetY)) <= 4 &&  ) return true;
        }

        private boolean isNearby(int x, int y){
            if (Math.abs(this.x  - x) < 2 && Math.abs(this.y - y) < 2) return true;
            return false;
        }

        private ArrayList<Point> silenceOptions(){
            ArrayList<Point> optionPoints = new ArrayList<Point>();
            for (int i = 1; i < 5; i++){
                Point point = new Point(x + i, y);
                if (point.isPoints() || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }

            for (int i = 1; i < 5; i++){
                Point point = new Point(x, y + i);
                if (point.isPoints() || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }

            for (int i = 1; i < 5; i++){
                Point point = new Point(x - i, y);
                if (point.isPoints() || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }

            for (int i = 1; i < 5; i++){
                Point point = new Point(x, y - i);
                if (point.isPoints() || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }
            return optionPoints;
        }

        private boolean isPoints(){
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()){
                Point point = pointIterator.next();
                if (isTheSame(point)) return true;
            }
            return false;
        }

        protected boolean isOnField(){
            if (x >= 0 && x < 15 && y >= 0 && y < 15 && field[y].charAt(x) == '.') return true;
            return false;
        }

        private boolean isTheSame(Point point){
            return x == point.getX() && y == point.getY();
        }

        private boolean isBack(Point parentPoint){
            for (int[] coordinate: parentPoint.route){
                if (x == coordinate[0] && y == coordinate[1])return true;
            }
            return false;
        }

        private Point theClosest(ArrayList<Point> points){
            Point closestPoint = points.get(0);
            int minDist = 30;
            for(Point point: points){
                int dist = Math.abs(x - point.getX()) + Math.abs(y - point.getY());
                if (dist < minDist){
                    minDist = dist;
                    closestPoint = point;
                }
            }
            return closestPoint;
        }

        private Point theFarthest(ArrayList<Point> points){
            Point farthestPoint = points.get(0);
            int maxDist = 0;
            for(Point point: points){
                int dist = Math.abs(x - point.getX()) + Math.abs(y - point.getY());
                if (dist > maxDist){
                    maxDist = dist;
                    farthestPoint = point;
                }
            }
            return farthestPoint;
        }

        private int countNeighbours(int level){
            ArrayList<Point> neighbours;
            neighbours = getDiamond(1);
            Iterator<Point> neighboursIterator = neighbours.iterator();
            while (neighboursIterator.hasNext()) {
                Point nextNeighbour = neighboursIterator.next();
                if (!nextNeighbour.isOnField() || nextNeighbour.isBack(myPoint) || !routeSet.add(nextNeighbour)) {
                    neighboursIterator.remove();
                }
            }
            if (level == 0) return neighbours.size();
            int count = 0;
            for(Point neighbour : neighbours){
                count = count + neighbour.countNeighbours(level - 1);
            }
            return neighbours.size() + count;
        }

        private Point getRoute(ArrayList<Point> optionSteps){
            if (optionSteps.size() == 1) return optionSteps.get(0);
            routeSet.clear();
            Point rightStep = optionSteps.get (0);//((int)(Math.random() * optionSteps.size()));
            int max = 0;
            int current;
            HashSet<int[]> tempoRoute = new HashSet<>();
            for(Point step: optionSteps){
                tempoRoute.addAll(myPoint.route);
                myPoint.route.addAll(step.route);
                current = step.countNeighbours(LEVEL_ROUTE);
                if(current > max){
                    max = current;
                    rightStep = step;
                    if (routeSet.contains(getTransitTarget())){
                        max += 50;
                    }
                }
                myPoint.route.clear();
                myPoint.route.addAll(tempoRoute);
                tempoRoute.clear();
            }
            return rightStep;
        }

        private ArrayList<Point> getDiamond(int radius){
            ArrayList<Point> border = new ArrayList<Point>();

            for (int i = 0; i < radius; i++){
                border.add(new Point(x + i, y + (radius - i)));
                border.add(new Point(x + (radius - i), y - i));
                border.add(new Point(x - i, y - (radius - i)));
                border.add(new Point(x - (radius - i), y + i ));
            }
            return border;
        }

        private Point getTransitTarget(){
            ArrayList<Point> transitTargets;
            if(torpCharge != 0 || points.size() > CHANCE_SHOT) transitTargets = targetPoint.getDiamond(6);
            else transitTargets = targetPoint.getDiamond(4);

            Iterator<Point> targetIterator = transitTargets.iterator();
            while (targetIterator.hasNext()) {
                Point nextPoint = targetIterator.next();
                if (!nextPoint.isOnField()) {
                    targetIterator.remove();
                }
            }
            if(transitTargets.isEmpty()) return targetPoint;
            return theClosest(transitTargets);
        }

        private String getMove(){
            ArrayList<Point> optionSteps;
            StringBuilder dir = new StringBuilder();
            optionSteps = getDiamond(1);

            Iterator<Point> pointIterator = optionSteps.iterator();
            while (pointIterator.hasNext()) {
                Point nextPoint = pointIterator.next();
                if (!nextPoint.isOnField() || nextPoint.isBack(myPoint)) {
                    pointIterator.remove();
                }
            }
            if (optionSteps.isEmpty()){
                route.clear();
                route.add(new int[]{x, y});
                return "SURFACE";
            }

            Point dirMove;
            if (points.size() > STRATEGY_TUMBLER && route.size() > 1){
                dirMove = getRoute(optionSteps);
            }else  dirMove = getTransitTarget().theClosest(optionSteps);
            route.add(new int[]{dirMove.getX(), dirMove.getY()});
            dir.append("MOVE " + stringDirection(dirMove));
            x = dirMove.getX();
            y = dirMove.getY();
            return dir.toString();
        }

        private String getSilence(){
            ArrayList<Point> optionPoints = new ArrayList<Point>();
            HashSet<int[]> tempoRoute = new HashSet<>();
            for (int i = 1; i < 5; i++){
                Point point = new Point(x + i, y);
                if (!point.isOnField() || point.isBack(this)) break;
                point.route.addAll(tempoRoute);
                tempoRoute.add(new int[]{point.getX(), point.getY()});
                optionPoints.add(point);
            }
            tempoRoute.clear();
            for (int i = 1; i < 5; i++){
                Point point = new Point(x, y + i);
                if (!point.isOnField() || point.isBack(this)) break;
                point.route.addAll(tempoRoute);
                tempoRoute.add(new int[]{point.getX(), point.getY()});
                optionPoints.add(point);
            }
            tempoRoute.clear();
            for (int i = 1; i < 5; i++){
                Point point = new Point(x - i, y);
                if (!point.isOnField() || point.isBack(this)) break;
                point.route.addAll(tempoRoute);
                tempoRoute.add(new int[]{point.getX(), point.getY()});
                optionPoints.add(point);
            }
            tempoRoute.clear();
            for (int i = 1; i < 5; i++){
                Point point = new Point(x, y - i);
                if (!point.isOnField() || point.isBack(this)) break;
                point.route.addAll(tempoRoute);
                tempoRoute.add(new int[]{point.getX(), point.getY()});
                optionPoints.add(point);
            }
            tempoRoute.clear();

            if (optionPoints.isEmpty()) return "SILENCE N 0";
            Point dirMove;
            if (points.size() > STRATEGY_TUMBLER){
                dirMove = theFarthest(optionPoints);
            }else dirMove = getRoute(optionPoints);

            int amountCells = Math.abs(x - dirMove.getX()) + Math.abs(y - dirMove.getY());
            String dir = stringDirection(dirMove);
            StringBuilder silenceOrder = new StringBuilder("SILENCE "
                    + dir + " " + amountCells);
            for (int i = 1; i <= amountCells; i++){
                if(dir.equals("E")) route.add(new int[]{x + i, y});
                if(dir.equals("W")) route.add(new int[]{x - i, y});
                if(dir.equals("S")) route.add(new int[]{x, y + i});
                if(dir.equals("N")) route.add(new int[]{x, y - i});
            }
            route.addAll(dirMove.route);
            x = dirMove.getX();
            y = dirMove.getY();
            return silenceOrder.toString();
        }

        private String stringDirection(Point point){
            if (point.getX() - x > 0) return "E";
            if (point.getX() - x < 0) return "W";
            if (point.getY() - y > 0) return "S";
            if (point.getY() - y < 0) return "N";
            return "SURFACE";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString(){
            return "{ " + x + " : " + y + " }";
        }
    }
}
