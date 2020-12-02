//
// Created by timo on 02.12.20.
//

#pragma once

#include <ostream>

// https://gist.github.com/arrieta/a3b8e4c893b53ec5397ad0d92158e8f1
namespace ansi {

#define MAKE_COLOR_MANIPULATOR(name, code)                               \
  template < typename CharT, typename Traits = std::char_traits<CharT> > \
  inline std::basic_ostream< CharT, Traits >&                            \
  name(std::basic_ostream< CharT, Traits >& os)                          \
  { return os << code; }

#define MAKE_COLOR_GROUP(name, foreground, background) \
    MAKE_COLOR_MANIPULATOR(name, "\033[" foreground "m") \
    MAKE_COLOR_MANIPULATOR(bright_##name, "\033[" foreground "m") \
    MAKE_COLOR_MANIPULATOR(bg_##name, "\033[" background "m") \
    MAKE_COLOR_MANIPULATOR(brightbg_##name, "\033[" background "m")

    MAKE_COLOR_MANIPULATOR(normal, "")
    MAKE_COLOR_MANIPULATOR(reset, "\033[m")

    MAKE_COLOR_GROUP(black, "30", "40")
    MAKE_COLOR_GROUP(red, "31", "41")
    MAKE_COLOR_GROUP(green, "32", "42")
    MAKE_COLOR_GROUP(yellow, "33", "43")
    MAKE_COLOR_GROUP(blue, "34", "44")
    MAKE_COLOR_GROUP(magenta, "35", "45")
    MAKE_COLOR_GROUP(cyan, "36", "46")
    MAKE_COLOR_GROUP(white, "37", "47")

}
