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
	class Cup
	{ 
		Texture2D catcher;
		KeyboardState keyboardState;
		Rectangle textureBounds;
		Rectangle screenBounds;
		int motion=1;
		int speed=5;

		public Cup(Texture2D texture,Rectangle screenBounds)
		{
			this.catcher=texture;
			this.screenBounds = screenBounds;
			textureBounds = new Rectangle ((screenBounds.Width - 50)/2, screenBounds.Height - 150,50,50);
		}

        /// <summary>
        /// Updates cup object
        /// </summary>
		public void Update(){
			keyboardState = Keyboard.GetState ();
			if(keyboardState.IsKeyDown(Keys.Right)){//move right
				motion = 1;
				textureBounds.X += (motion*speed);
			}
			if(keyboardState.IsKeyDown(Keys.Left)){//move left
				motion = -1;
				textureBounds.X += (motion*speed);
			}
            //Kepp cup within the screen area
			if (textureBounds.X < 0)
				textureBounds.X = 0;
			if (textureBounds.X+textureBounds.Width > screenBounds.Width)
				textureBounds.X = screenBounds.Width-textureBounds.Width;
		}

        /// <summary>
        /// Draws cup object
        /// </summary>
        /// <param name="spriteBatch"></param>
		public void Draw(SpriteBatch spriteBatch){
			spriteBatch.Draw (catcher, textureBounds, Color.White);
		}

        /// <summary>
        /// Returns cup bounds
        /// </summary>
        /// <returns></returns>
        public Rectangle getCupBounds()
        {
            return textureBounds;
        }
	}
}

