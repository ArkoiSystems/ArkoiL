//
// Created by timo on 7/31/20.
//

#include "error.h"

std::ostream &operator<<(std::ostream &out, const Error &error) {
    out << error.sourcePath << ":" << (error.startLine + 1) << " "
        << error.causeMessage << std::endl;

    std::vector<std::string> lines;
    split(error.sourceCode, lines, '\n');

    unsigned int startLineChar = 0;
    unsigned int endLineChar = 0;
    for (auto lineIndex = 0; lineIndex < error.endLine; lineIndex++) {
        if (lineIndex <= error.startLine)
            startLineChar += lines[lineIndex].size() + 1;
        endLineChar += lines[lineIndex].size() + 1;
    }

    auto startLineDifference = error.startChar - startLineChar;
    auto endLineDifference = error.endChar - endLineChar;

    auto biggestNumber = std::to_string(error.endLine + 3);
    for (auto index = error.startLine; index < error.endLine + 3; index++) {
        if(index == 0)
            continue;

        auto currentNumber = std::to_string(index);
        auto whitespaces = biggestNumber.size() - currentNumber.size();

        auto line = lines[index - 1];

        out << "> " << std::string(whitespaces, ' ') << index << " | " << line
            << std::endl;
        if (error.startLine == index - 1) {
            out << "  " << std::string(biggestNumber.size(), ' ') << "   "
                << std::string(startLineDifference, ' ')
                << std::string(1, '^')
                << std::string((endLineDifference - startLineDifference) - 1, '~')
                << std::endl;
        }
    }

    return out;
}