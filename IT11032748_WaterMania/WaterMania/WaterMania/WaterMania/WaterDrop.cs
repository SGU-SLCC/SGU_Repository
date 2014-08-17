using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Storage;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Media;

namespace WaterMania
{
	class WaterDrop
	{
		Texture2D waterDrop;
		Rectangle screenBounds;
        Rectangle textureBounds;
		bool outsideScreen=false;//If the drop has fallen on the table
        bool caught = false;

        public WaterDrop(Texture2D texture, Rectangle screenBounds, int assignedXValue)
        {
			this.waterDrop=texture;
			this.screenBounds = screenBounds;
            textureBounds = new Rectangle(assignedXValue, 0, 10, 15);
		}

        /// <summary>
        /// Updates water drop object
        /// </summary>
        /// <param name="cupBounds"></param>
		public void Update(Rectangle cupBounds){
            textureBounds.Y = textureBounds.Y + 1;
            //if drop falls within cup
            if (textureBounds.Y == screenBounds.Height - 150 - textureBounds.Height && (textureBounds.X > cupBounds.X && textureBounds.X<cupBounds.X+cupBounds.Width))
                caught = true;
            else if (textureBounds.Y > screenBounds.Height - 100) // ifdrop misses cup
				outsideScreen = true;
		}

        /// <summary>
        /// Draws water drop
        /// </summary>
        /// <param name="spriteBatch"></param>
		public void Draw(SpriteBatch spriteBatch){
			spriteBatch.Draw (waterDrop, textureBounds, Color.White);
		}

        /// <summary>
        /// Returns if drop missed the cup
        /// </summary>
        /// <returns></returns>
		public bool IsOutside(){
			return outsideScreen;
		}

        /// <summary>
        /// Returns if the drop was caught
        /// </summary>
        /// <returns></returns>
        public bool IsCaught()
        {
            return caught;
        }

        /// <summary>
        /// Returns the current position of the water drop
        /// </summary>
        /// <returns></returns>
        public Vector2 getPosition()
        {
            return new Vector2(textureBounds.X, textureBounds.Y+textureBounds.Height);
        }
	}
}

