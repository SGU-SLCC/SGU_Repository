using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;
using System.Collections;

namespace WaterMania
{
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class Game1 : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        Texture2D waterDrop;
        Texture2D cup;
        Texture2D background;
        Texture2D splash;
        ArrayList drops;
        Cup catcher;
        Rectangle screenBounds;
        float timer = 10;         //Initialize a 10 second timer
        const float TIMER = 10;
        float splashTimer = 10;         //Initialize a 10 second timer
        const float SPLASHTIMER = 10;
        int dropSpeed = 5;
        int splashSpeed = 10;
        Vector2 splashPosition=new Vector2();
        int score = 0;
        int lives = 3;
        SpriteFont font;
        SpriteFont fontStatus;
        String lose = "Too Bad!";
        String loseInstruction = "Press 'Space' to start over...";
        KeyboardState keyboardState;

        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
            graphics.IsFullScreen = false;
            //Set screen size
            graphics.PreferredBackBufferWidth = 450;
            graphics.PreferredBackBufferHeight = 470;
            //Set screen bounds
            screenBounds = new Rectangle(0, 0, graphics.PreferredBackBufferWidth, graphics.PreferredBackBufferHeight);            
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here

            base.Initialize();
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be use to draw textures
            spriteBatch = new SpriteBatch(GraphicsDevice);

            //Load content
            waterDrop = Content.Load<Texture2D>("drop");
            cup = Content.Load<Texture2D>("cup");
            background = Content.Load<Texture2D>("table");
            splash = Content.Load<Texture2D>("splash");
            font = Content.Load<SpriteFont>("SpriteFontDetails");
            fontStatus = Content.Load<SpriteFont>("SpriteFontStatus");

            //Create game objects
            catcher = new Cup(cup, screenBounds);
            drops = new ArrayList();
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            // Allows the game to exit
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed)
                this.Exit();

            //If player has lost
            if (lives == 0)
            {
                keyboardState = Keyboard.GetState ();
                //If space is pressed, restart the game
                if (keyboardState.IsKeyDown(Keys.Space))
                {
                    lives = 3;
                    score = 0;
                    catcher = new Cup(cup, screenBounds);
                    drops = new ArrayList();
                }
            }
            else//If game is still going
            {
                //Update game objects
                catcher.Update();
                int i = 0;
                foreach (WaterDrop d in drops)
                {
                    //If drop has either met table or cup
                    if (d.IsOutside() || d.IsCaught())
                    {
                        if (d.IsCaught())//If cup, increase score
                            score++;
                        if (d.IsOutside())//If table, decrease lives
                            lives--;
                        splashPosition = d.getPosition();//get the position of drop to display the water splash
                        ArrayList temp = drops;
                        drops = new ArrayList();
                        int j = 0;
                        //create a new water droplets list without removed drop
                        foreach (WaterDrop drop in temp)
                        {
                            if (j != i)
                                drops.Add(drop);
                            j++;
                        }
                    }
                    i++;
                }

                //Periodically generate a water drop
                float elapsed = (float)gameTime.ElapsedGameTime.TotalSeconds;
                timer -= elapsed * dropSpeed;
                if (timer < 0)
                {
                    //Timer expired, execute action
                    AddDrop();
                    timer = TIMER;   //Reset Timer
                }

                foreach (WaterDrop d in drops)
                {
                    d.Update(catcher.getCupBounds());
                }
            }
            base.Update(gameTime);
        }

        /// <summary>
        /// Adds a drop to the drops list
        /// </summary>
        private void AddDrop()
        {
            Random random = new Random();
            int randomValue = random.Next(graphics.PreferredBackBufferWidth - 10);
            drops.Add(new WaterDrop(waterDrop, screenBounds, randomValue));
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            // Clear the backbuffer
            graphics.GraphicsDevice.Clear(Color.White);

            spriteBatch.Begin();

            //if player has lost draw score screen
            if (lives == 0)
            {
                spriteBatch.DrawString(fontStatus, "Score: "+score, new Vector2((graphics.PreferredBackBufferWidth - lose.Length - 150) / 2, (graphics.PreferredBackBufferHeight / 2 - 75)), Color.CornflowerBlue);
                spriteBatch.DrawString(font, loseInstruction, new Vector2((graphics.PreferredBackBufferWidth - loseInstruction.Length) / 2 - 150, (graphics.PreferredBackBufferHeight / 2 )+25), Color.CornflowerBlue);
            }
            else//if game is still going
            {
                //draw background
                spriteBatch.Draw(background, new Rectangle(0, 0, graphics.PreferredBackBufferWidth, graphics.PreferredBackBufferHeight), Color.White);
                //Show score and lives
                spriteBatch.DrawString(font, "Score: "+score, new Vector2(10,10), Color.SandyBrown);
                spriteBatch.DrawString(font, "Lives: "+lives, new Vector2(10,30), Color.SandyBrown);
                //Draw game objects
                catcher.Draw(spriteBatch);
                foreach (WaterDrop d in drops)
                {
                    d.Draw(spriteBatch);
                }

                //Draw splash where drop fell
                if (splashPosition.X != 0 && splashPosition.Y != 0)
                {
                    spriteBatch.Draw(splash, new Rectangle((int)splashPosition.X, (int)splashPosition.Y, 30, 20), Color.White);
                    float elapsed = (float)gameTime.ElapsedGameTime.TotalSeconds;
                    splashTimer -= elapsed * splashSpeed;
                    if (splashTimer < 0)
                    {
                        //Timer expired, execute action
                        splashPosition = new Vector2();
                        splashTimer = SPLASHTIMER;   //Reset Timer
                    }
                }
            }
            spriteBatch.End();

            base.Draw(gameTime);
        }
    }
}
