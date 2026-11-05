package com.example

import com.example.model.LevelRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun levelConfigurations_areValid() {
    val levels = LevelRepository.levels
    assertEquals(12, levels.size)
    levels.forEach { level ->
      assertTrue("Level ${level.levelNumber} must have positive moves", level.maxMoves > 0)
      assertTrue("Level ${level.levelNumber} must have target score", level.targetScore > 0)
    }
  }
}
