// *****************************************
// *                                       *
// *  RoboCup Image Acquisition Front End  *
// *                                       *
// *****************************************

#ifndef _FrontEnd_
#define _FrontEnd_

#include "stdafx.h"
#include "vision2015.h"

using namespace std;

// ***************
// *             *
// *  Constants  *
// *             *
// ***************
//
// These constants define the number of bits for Y, U, and V after summing pixels
enum
{
  YBits   = 10,
  YRange  = 1 << YBits,
  YMax    = 255 << (YBits - 8),

  UVBits  = 9,
  UVRange = 1 << UVBits,
  UVMask   = UVRange - 1,
  UVZero  = 1 << (UVBits - 1),
  UVMax   = 255 << (UVBits - 8),

  YCoeffBits = 16 - YBits + UVBits
};

// *****************************
// *                           *
// *  An SSE2 Double Quadword  *
// *                           *
// *****************************

struct DQWord
{
  int16_t words[8];

  DQWord() {}

  void load(int x)
  {
    for (int i = 0; i < 8; ++i)
      words[i] = (int16_t)x;
  }

  void load(int x, int y)
  {
    for (int i = 0; i < 8; i += 2)
    {
      words[i    ] = (int16_t)x;
      words[i + 1] = (int16_t)y;
    }
  }

};

// *************************************
// *                                   *
// *  Parameters for a Computed Color  *
// *                                   *
// *************************************
//
// Each of these parameters is a double quadword containing the U parameters
// in the even words and the V parameters in the odd words. The ASM code
// evaluates all fuzzy thresholds for U and V in parallel, which means that
// U and V have to be both min values or both max values. If they differ
// (e.g. orange), then V is inverted.
struct ColorParams
{
  DQWord darkZero;      // UV values that map to fuzzy 0 at Y = 0
  DQWord yCoeff;        // Change in darkZero values per unit Y above 0
  DQWord fuzzySpread;   // Fuzzy spread in UV units
  DQWord inverseFuzzy;  // 1 / spread, u16.8

  // These functions are to fetch parameters for the C++ version that is not SIMD parallel
  int darkUV0   (int uv) const { return darkZero    .words[uv];}
  int yuvCoeff  (int uv) const { return yCoeff      .words[uv];}
  int fuzzyUV   (int uv) const { return fuzzySpread .words[uv];}
  int invFuzzyUV(int uv) const { return inverseFuzzy.words[uv];}

  // For C++, fetch the UV value that maps tp fuzzy 0 and the specified Y
  int uv0(int uv, int y) const { return darkUV0(uv) + (y * yuvCoeff(uv) >> 16);}

  // For C++, get the fuzzy value specifying how much x is above the fuzzy threshold
  // 0 <= result <= 255
  int fuzzyMin(int uv, int y, int x) const
  {
    return min(max(x - uv0(uv, y), 0), fuzzyUV(uv)) * invFuzzyUV(uv) >> 8;
  }

  // For C++, get the fuzzy value specifying how much x is below the fuzzy threshold
  // 0 <= result <= 255
  int fuzzyMax(int uv, int y, int x) const
  {
    return min(max(uv0(uv, y) - x, 0), fuzzyUV(uv)) * invFuzzyUV(uv) >> 8;
  }

  // For C++, get the score corresponding to (y,u,v), which is the fuzzy AND of
  // the amount that U and V are above the threshold
  uint8_t scoreMin(int y, int u, int v) const
  {
    return (uint8_t)min(fuzzyMin(0, y, u), fuzzyMin(1, y, v));
  }

  // For C++, get the score corresponding to (y,u,v), which is the fuzzy AND of
  // the amount that U and V are below the threshold
  uint8_t scoreMax(int y, int u, int v) const
  {
    return (uint8_t)min(fuzzyMax(0, y, u), fuzzyMax(1, y, v));
  }

  // Load this color parameter from the indicated values. The fuzzy spreads are
  // positive if the threshold is a minimum value and negative if the threshold
  // is a maximum value.
  void load(float darkU0, float darkV0, float lightU0, float lightV0, float fuzzyU, float fuzzyV);
};

// A complete set of color parameters
struct Colors
{
  ColorParams white;
  ColorParams green;
  ColorParams orange;
};

// ***************************************
// *                                     *
// *  Front End Image Acquire Functions  *
// *                                     *
// ***************************************
//
// The ASM and C++ have identical function signatures.
// The source is a YUYV image. There are no pixel alignment requirements, although
// operation may be faster if source is QWORD or DQWORD aligned.
// width and height refer to the output images. The low three bits of width are ignored and
// assumed to be zero.
// pitch is source image row pitch in bytes, and can be >= width
// The destination is four or five images, concatenated with no padding:
//    Y image, 16-bit pixels
//    white image, 8-bit pixels
//    green image, 8-bit pixels
//    orange image, 8-bit pixels
//    optional image reulting from color table lookup
extern "C" uint32_t
  newAcquire(const uint8_t* source, int width, int height, int pitch, const Colors* colors,
             uint8_t* dest, uint8_t* colorTable = 0);

uint32_t
  testAcquire(const uint8_t* source, int width, int height, int pitch, const Colors* colors,
              uint8_t* dest, uint8_t* colorTable = 0);

#endif