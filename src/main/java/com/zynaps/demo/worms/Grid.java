package com.zynaps.demo.worms;

import java.awt.image.BufferedImage;

class Grid {

    private static final int FREE = 0x000000;
    private static final int FOOD = 0x000044;
    private static final int WALL = 0x0088FF;
    private static final int OBSTACLE = 0x0000FF;
    private static final int CREATURE = 0xFF0000;
    private static final int TRAIL_LENGTH = 16;
    private static final int TRAIL_MASK = 15;
    private static final int GRID_SIZE = 256;

    private final int[] terrain;
    private final int[] pixels;
    private final int[] trail;
    private int trailIndex;

    public Grid() {
        terrain = new int[GRID_SIZE * GRID_SIZE];
        pixels = new int[GRID_SIZE * GRID_SIZE];
        trail = new int[TRAIL_LENGTH];
        generate();
        reset();
    }

    public boolean isFree(int x, int y) {
        int gs = get(x, y);
        return (gs == FREE) || (gs == FOOD);
    }

    public boolean isObstacle(int x, int y) {
        int gs = get(x, y);
        return (gs == WALL) || (gs == OBSTACLE);
    }

    public boolean isFood(int x, int y) {
        return get(x, y) == FOOD;
    }

    public int get(int x, int y) {
        return pixels[y * GRID_SIZE + x];
    }

    public void set(int x, int y) {
        pixels[trail[TRAIL_MASK & trailIndex++] = GRID_SIZE * y + x] = CREATURE;
        pixels[trail[TRAIL_MASK & trailIndex]] = FREE;
    }

    public void reset() {
        trailIndex = 0;
        int origin = (GRID_SIZE >> 1) * GRID_SIZE + (GRID_SIZE >> 1);
        for (int i = 0; i < trail.length; ++i) {
            trail[i] = origin;
        }
        System.arraycopy(terrain, 0, pixels, 0, terrain.length);
    }

    public void generate() {
        Automata automata = new Automata(GRID_SIZE - 2, GRID_SIZE - 2);
        automata.seed = String.valueOf(System.currentTimeMillis());
        automata.randomFillPercent = 49;
        int[][] map = automata.generateMap();

        for (int x = 0; x < map.length; ++x) {
            for (int y = 0; y < map[0].length; ++y) {
                terrain[y * GRID_SIZE + x] = map[x][y] == 1 ? OBSTACLE : FOOD;
            }
        }

        for (int p = 0; p < GRID_SIZE; ++p) {
            terrain[p] = terrain[0xFF * GRID_SIZE + p] = terrain[p * GRID_SIZE] = terrain[p * GRID_SIZE + 0xFF] = WALL;
        }
    }

    public void drawToBitmap(BufferedImage image) {
        image.setRGB(0, 0, GRID_SIZE, GRID_SIZE, pixels, 0, GRID_SIZE);
    }
}
