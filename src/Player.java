import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static final int CHANCE_SHOT = 50;
    private static final int SONAR_TRIGGER = 25;
    private static final int STRATEGY_TUMBLER = 0;
    private static final int LEVEL_ROUTE = 100;
    private static final int MINE_GAP = 3;
    private static final int TRIGGER_MIN = 3;


    private static String[] field;
    private static ArrayList<Point> points = new ArrayList<>();
    private static Point myPoint;
    private static Point targetPoint;
    private static int lastSonar = 0;
    private static int oppTestLife = 6;
    private static HashSet<Point> routeSet = new HashSet<Point>();
    private static ArrayList<Point> explosions = new ArrayList<>();
    private static HashSet<Point> mines = new HashSet<>();

    private static int myLife;
    private static int oppLife;
    private static int torpedoCooldown;
    private static int sonarCooldown;
    private static int silenceCooldown;
    private static int mineCooldown;
    private static String sonarResult;
    private static String opponentOrders;

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
            myLife = in.nextInt();
            oppLife = in.nextInt();
            torpedoCooldown = in.nextInt();
            sonarCooldown = in.nextInt();
            silenceCooldown = in.nextInt();
            mineCooldown = in.nextInt();
            sonarResult = in.next();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            opponentOrders = in.nextLine();

            sonarHandler(sonarResult);
            if(opponentOrders.contains("SURFACE")) surfaceHandler();

            int damage = oppLife - oppTestLife;
            ArrayList<Point> zoneExplosions = new ArrayList<>();
            if(!explosions.isEmpty()){
                for (Point explosion : explosions){
                    zoneExplosions.addAll(explosion.getZoneExplosion());
                }
                zoneExplosions.addAll(explosions);
            }

            if (damage == 0) {
                Iterator<Point> iterator = points.iterator();
                while (iterator.hasNext()){
                    Point point = iterator.next();
                    if (zoneExplosions.contains(point)) iterator.remove();
                }
                torpedoHandler();
                moveHandler();


            }




            if ()


                if(opponentOrders.contains("TORPEDO") || opponentOrders.contains("TRIGGER")) attackHandler();
                if(opponentOrders.contains("MOVE")) moveHandler();
                if(opponentOrders.contains("SILENCE")) silenceHandler();





            System.err.println("Points >> " + points.size());
            System.err.println(points);

            String action;
            action = action();
            System.out.println(action);

            oppTestLife = oppLife;
            System.err.println("Points >> " + points.size());
            System.err.println(points);

        }

    }
    protected static void fieldCreate(int height, Scanner in) {
        field = new String[height];
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            field[i] = line;
        }
    }

    protected static void pointsCreate(){
        for (int i = 0; i < 15; i++){
            for (int j = 0; j < 15; j++){
                if (field[i].charAt(j) == '.') points.add(new Point(j, i));
            }
        }

    }

    protected static void myPointCreate(){
        int nSector = makeSonar();

        while (true){
            Point startPoint = points.get((int)(Math.random() * points.size()));
            if (startPoint.isSector(nSector)){
                myPoint = new Point(startPoint.getX(), startPoint.getY());
                break;
            }
        }
    }

    protected static void sonarHandler(String sonarResult){
        if(sonarResult.equals("Y")){
            Iterator<Point> iterator = points.iterator();
            while (iterator.hasNext()) {
                Point point = iterator.next();
                if (!point.isSector(lastSonar)) {
                    iterator.remove();
                }
            }
        }
        if(sonarResult.equals("N")){
            Iterator<Point> iterator = points.iterator();
            while (iterator.hasNext()) {
                Point point = iterator.next();
                if (point.isSector(lastSonar)) {
                    iterator.remove();
                }
            }
        }
        lastSonar = 0;
    }

    protected static void surfaceHandler(){
        if (opponentOrders.contains("SURFACE")) {
            int indexSurface = opponentOrders.indexOf("SURFACE");
            String surface = opponentOrders.substring(indexSurface);
            int sector = Integer.parseInt(surface.substring(8, 9));
            Iterator<Point> iterator = points.iterator();
            while (iterator.hasNext()) {
                Point point = iterator.next();
                if (!point.isSector(sector)) {
                    iterator.remove();
                }
            }
            oppTestLife -= 1;
        }
    }

    private static void torpedoHandler(){
        if (opponentOrders.contains("TORPEDO")){
            Point attackPoint = getAttackPoint("TORPEDO");
            Iterator<Point> iterator = points.iterator();
            while (iterator.hasNext()){
                Point point = iterator.next();
                if (point.isDistShot(attackPoint.getX(), attackPoint.getY()));
            }
            explosions.add(attackPoint);
        }
    }

    private static Point getAttackPoint(String attack){
        int startIndex = opponentOrders.indexOf(attack);
        String tempoString = opponentOrders.substring(startIndex);
        int finishIndex = tempoString.indexOf("|");
        String order;
        if (finishIndex >= 0){
            order = tempoString.substring(0, finishIndex);
        }else order = tempoString;

        String[] words = order.split(" ");
        Point attackCoordinates = new Point(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
        return attackCoordinates;
    }

    private static void attackHandler() {
        System.err.println("Torpedo " + points);

        System.err.println("Trigger " + points);
        if(opponentOrders.contains("TRIGGER")) explosions.add(getAttackPoint("TRIGGER"));


        int damage = oppTestLife - oppLife;
        System.err.println("Damage " + damage);
        System.err.println("Points  " + points);
        System.err.println("ZoneExplosions " + zoneExplosions);
        if (damage == 0){
            Iterator<Point> iterator = points.iterator();
            while (iterator.hasNext()){
                Point point = iterator.next();
                if (zoneExplosions.contains(point)) iterator.remove();
            }
        }else {
            Iterator<Point>  iterator = points.iterator();
            while (iterator.hasNext()){
                Point point = iterator.next();
                if (!zoneExplosions.contains(point)) iterator.remove();
                else{
                   int countDammage = 0;
                   for (Point checkPoint : zoneExplosions){
                       if (checkPoint.equals(point)) countDammage++;
                   }
                   if (countDammage != damage) iterator.remove();
                }
            }
        }
        System.err.println("after damage " + points);
        explosions.clear();
    }

    protected static void moveHandler(){
        if (opponentOrders.contains("MOVE")) {
            int indexMove = opponentOrders.indexOf("MOVE");
            String move = opponentOrders.substring(indexMove);
            char direction = move.charAt(5);
            Iterator<Point> iterator = points.iterator();
            while (iterator.hasNext()) {
                Point point = iterator.next();
                if (!point.checkMove(direction)) {
                    iterator.remove();
                }
            }
        }
    }

    protected static void silenceHandler(){
        if (opponentOrders.contains("SILENCE")) {
            ListIterator<Point> iterator = points.listIterator();
            while (iterator.hasNext()) {
                Point point = iterator.next();
                ArrayList<Point> newPoints = point.silenceOptions();
                for(Point checkPoint: newPoints){
                    iterator.add(checkPoint);
                }
            }
        }
    }

    protected static Point getTargetPoint(){
        return points.get(0);
    }

    protected static int makeSonar(){
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
    protected static String action() {
        StringBuilder myOrder = new StringBuilder();
        targetPoint = getTargetPoint();
        ;

        if (myPoint.isReadyShot(targetPoint)) {
            torpedoCooldown = 3;
            myOrder.append("TORPEDO " + targetPoint.getX() + " " + targetPoint.getY() + " | ");
            explosions.add(targetPoint);
        }

        if(mineCooldown == 0){
            myOrder.append(myPoint.makeMine());
            mineCooldown = 3;
        }

        myOrder.append(myPoint.makeMove());
        if (myOrder.toString().contains("SURFACE")) {
            myOrder.append(" | " + myPoint.makeMove());
        }
        if (torpedoCooldown <= 3 && torpedoCooldown > 0) {
            torpedoCooldown--;
            myOrder.append(" TORPEDO");
        }
        else if (silenceCooldown <= 6 && silenceCooldown > 0) myOrder.append(" SILENCE");
        else if (sonarCooldown <= 4 && sonarCooldown > 0 && points.size() < SONAR_TRIGGER) myOrder.append(" SONAR");
        else if (mineCooldown <= 3 && mineCooldown > 0) myOrder.append(" MINE");

        targetPoint = getTargetPoint();
        ;

        if (myPoint.isReadyShot(targetPoint)) {
            torpedoCooldown = 3;
            myOrder.append(" | TORPEDO " + targetPoint.getX() + " " + targetPoint.getY());
            explosions.add(targetPoint);
        }
        if (points.size() > SONAR_TRIGGER && sonarCooldown == 0){
            myOrder.append(" | SONAR " + makeSonar());
            sonarCooldown = 4;
        }
        if (silenceCooldown == 0 && myPoint.silenceOptions().size() > 8) myOrder.append(" | " + myPoint.makeSilence());
        if (myPoint.isReadyShot(targetPoint)) {
            torpedoCooldown = 3;
            myOrder.append(" | TORPEDO " + targetPoint.getX() + " " + targetPoint.getY());
            explosions.add(targetPoint);
        }
        myOrder.append(myPoint.makeTrigger());

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

        protected boolean isReadyShot(Point targetPoint){
            return (isDistShot(targetPoint.getX(), targetPoint.getY()))
                    &&points.size() <= CHANCE_SHOT && torpedoCooldown == 0 && !equals(targetPoint);
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
                if (points.contains(point) || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }

            for (int i = 1; i < 5; i++){
                Point point = new Point(x, y + i);
                if (points.contains(point)  || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }

            for (int i = 1; i < 5; i++){
                Point point = new Point(x - i, y);
                if (points.contains(point)  || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }

            for (int i = 1; i < 5; i++){
                Point point = new Point(x, y - i);
                if (points.contains(point)  || !point.isOnField() || point.isBack(this)) break;
                optionPoints.add(point);
            }
            return optionPoints;
        }

        protected boolean isOnField(){
            if (x >= 0 && x < 15 && y >= 0 && y < 15 && field[y].charAt(x) == '.') return true;
            return false;
        }

        private boolean isBack(Point parentPoint){
            for (int[] coordinate: parentPoint.route){
                if (x == coordinate[0] && y == coordinate[1])return true;
            }
            return false;
        }

        private HashSet<Point>  getZoneExplosion(){
            HashSet<Point> zoneExplosion = new HashSet<>();
            for (int i = -1; i <= 1; i++){
                for (int j = -1; j <= 1; j++){
                    zoneExplosion.add(new Point(x + i, y + j));
                }
            }
            return zoneExplosion;
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

        private HashSet<Point> getDistTorpedo(int radius){
            HashSet<Point> dist = new HashSet<>();

            for (int i = -radius; i <= radius; i++){
                for (int j = 0 - (radius - Math.abs(i)); j <= 0 + (radius - Math.abs(i)); j++){
                    dist.add(new Point(x + i, y + j));
                }
            }
            return dist;
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
            if(torpedoCooldown > 0 || points.size() > CHANCE_SHOT) transitTargets = targetPoint.getDiamond(6);
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

        private String makeMove(){
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

        private String makeMine(){
            ArrayList<Point> minePlaces = getDiamond(1);
            for(Point point : minePlaces){
                if(point.isOnField() && point.getX() % MINE_GAP == 1
                        && point.getY() % MINE_GAP == 1 && !mines.contains(point)){
                    mines.add(point);
                    return " | MINE " +  stringDirection(point) + " | ";
                }
            }
            return "";
        }

        private String makeSilence(){
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

        private String makeTrigger(){
            if(points.size() < TRIGGER_MIN){
                for (Point point : points){
                    for (Point nextPoint : point.getZoneExplosion()){
                        if (mines.contains(nextPoint) && !myPoint.getZoneExplosion().contains(nextPoint)) {
                            explosions.add(nextPoint);
                            mines.remove(nextPoint);
                            return " | TRIGGER " + nextPoint.getX() + " " + nextPoint.getY();
                        }
                    }
                }
            }
            return "";
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
