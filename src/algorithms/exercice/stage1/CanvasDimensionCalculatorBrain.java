package algorithms.exercice.stage1;

import characteristics.Parameters;

public class CanvasDimensionCalculatorBrain extends SquareWalkBrain {

    double distance = 0;
    private static int LEFT_TURN_STATE = 3;
    private static int MOVE_TO_CORNER_STATE = 4;
    double virageCounter = 0;
    double longueur = 0;
    double largeur = 0;
    private boolean positionCorrectionFinished = false;

    @Override
    public void step() {
        if (!positionCorrectionFinished) {
            goToRightLeftCorner();
        } else {
            doWidthAndHeightComputationWalk();
        }
    }

    private void doWidthAndHeightComputationWalk() {
        if (virageCounter != 2)  {
            if (state ==  RIGHT_TURN_STATE && !isSameDirection(getHeading(), oldAngle+(Math.PI/2))) {
                stepTurn(Parameters.Direction.RIGHT);
                return;
            }
            if (state ==  RIGHT_TURN_STATE && isSameDirection(getHeading(), oldAngle+(Math.PI/2))) {
                oldAngle = getHeading();
                state = MOVE_STATE;
                virageCounter++;
                return;
            }

            if (state == MOVE_STATE && wallDetected()) {
                state = RIGHT_TURN_STATE;
                return;
            }
            if (state == MOVE_STATE && !wallDetected()) {
                moveAux();
                return;
            }
        }
        sendLogMessage("Distance: " + distance + " Longueur: " + longueur + " Largeur: " + largeur);
    }

    private void moveAux() {
        double robotSpeed = Parameters.teamASecondaryBotSpeed;
        if (virageCounter == 0) {
            longueur += robotSpeed;
        } else {
            largeur += robotSpeed;
        }
        distance += robotSpeed;
        move();
    }

    private void goToRightLeftCorner() {
        // init state
        if (state == INIT_STATE && !isSameDirection(getHeading(), Parameters.NORTH)) {
            stepTurn(Parameters.Direction.LEFT);
            sendLogMessage("Robot are turning left");
            return;
        }
        if (state == INIT_STATE && isSameDirection(getHeading(), Parameters.NORTH)) {
            state = MOVE_STATE;
            return;
        }

        // move state
        if (state == MOVE_STATE && !wallDetected()) {
            move();
            sendLogMessage("Robot are moving");
            return;
        }
        if (state == MOVE_STATE && wallDetected()) {
            state = LEFT_TURN_STATE;
            sendLogMessage("Robot about to turn left");
            return;
        }

        // left turn state
        if (state == LEFT_TURN_STATE && !isSameDirection(getHeading(), Parameters.WEST)) {
            stepTurn(Parameters.Direction.LEFT);
            sendLogMessage("Robots are turning left !! ");
            return;
        }
        if (state == LEFT_TURN_STATE && isSameDirection(getHeading(), Parameters.WEST)) {
            state = MOVE_TO_CORNER_STATE;
            sendLogMessage("about to move to corner");
            return;
        }

        // move to corner state
        if (state == MOVE_TO_CORNER_STATE && !wallDetected()) {
            move();
            sendLogMessage("Robot are moving to corner");
            return;
        }
        if (state == MOVE_TO_CORNER_STATE && wallDetected()) {
            state = RIGHT_TURN_STATE;
            sendLogMessage("Robot about to turn right");
            return;
        }

        // right turn state
        if (state == RIGHT_TURN_STATE && !isSameDirection(getHeading(), Parameters.EAST)) {
            stepTurn(Parameters.Direction.RIGHT);
            sendLogMessage("Robot are turning right");
            return;
        }
        if (state == RIGHT_TURN_STATE && isSameDirection(getHeading(), Parameters.EAST)) {
            positionCorrectionFinished = true;
            sendLogMessage("Robot are in the right position");
            state = MOVE_STATE;
            return;
        }
    }

}