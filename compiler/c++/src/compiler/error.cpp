//
// Created by timo on 7/31/20.
//

#include "error.h"

std::ostream &operator<<(std::ostream &out, const Error &error) {
    out << error.sourcePath << ":" << (error.startLine + 1)
        << " " << error.causeMessage;
    return out;
}