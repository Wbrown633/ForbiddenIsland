import java.awt.Color;

import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.WorldImage;

// a class to represent a player of the game
class Player {
  Cell location;
  int xpos;
  int ypos;
  WorldImage image;
  
  Player(Cell location) {
     this.location = location;
     this.xpos = location.x;
     this.ypos = location.y;
     this.image = new CircleImage(ForbiddenIslandWorld.CELL_SIZE, OutlineMode.SOLID, Color.ORANGE);
  }
  
  // Draws this player onto the background
  WorldImage drawAt(WorldImage background) {
    return new OverlayImage(new CircleImage(ForbiddenIslandWorld.CELL_SIZE/2, OutlineMode.SOLID, Color.BLACK), background);
  }
  
  // move this player from the current cell to the cell in the given direction
  public Player movePlayer(String direction) {
    // set the cell the player wants to move to
    Cell targetCell;
    if (direction.equals("up")) {
      targetCell = this.location.top;
    }
    else if (direction.equals("down")) {
      targetCell = this.location.bottom;
    }
    else if (direction.equals("right")) {
      targetCell = this.location.right;
    }
    else if (direction.equals("left")) {
      targetCell = this.location.left;
    }
    else {
      throw new IllegalArgumentException("Not a valid direction to move");
    }
    
    // move the player to the cell if it isn't flooded
    if (!targetCell.isFlooded) {
      return new Player(targetCell);
    }
    
    // if we can't move the player return this player
    else {
      return this;
    }
  }
  
  // is the player on the same cell as the given target? 
  public boolean isTouching(Target t) {
    return this.location.isTouching(t.location);
  }
  
  

}
