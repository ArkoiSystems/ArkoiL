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
        if (lineIndex < error.startLine)
            startLineChar += lines[lineIndex].size() + 1;
        endLineChar += lines[lineIndex].size() + 1;
    }

    auto startLineDifference = error.startChar - startLineChar;
    auto endLineDifference = error.endChar - endLineChar;

    auto biggestNumber = std::to_string(error.endLine + 3);
    auto currentLineChar = 0;
    for (auto lineIndex = 0; lineIndex < error.endLine + 3; lineIndex++) {
        if (lineIndex == 0)
            continue;
        currentLineChar += lines[lineIndex].size() + 1;
        if (lineIndex < error.startLine)
            continue;

        auto currentNumber = std::to_string(lineIndex);
        auto whitespaces = biggestNumber.size() - currentNumber.size();

        auto line = lines[lineIndex - 1];
        rtrim(line);

        out << "> " << std::string(whitespaces, ' ') << lineIndex << " | " << line << std::endl;
        if (error.startLine == lineIndex - 1 && error.endLine == lineIndex - 1) {
            out << "  " << std::string(biggestNumber.size(), ' ') << " | "
                << std::string(startLineDifference, ' ')
                << std::string(1, '^')
                << std::string((endLineDifference - startLineDifference) - 1, '~')
                << std::endl;
        } else if (error.startLine == lineIndex - 1 && error.endLine != lineIndex - 1) {
            out << "  " << std::string(biggestNumber.size(), ' ') << " | "
                << std::string(startLineDifference, ' ')
                << std::string(1, '^')
                << std::string((line.size() - startLineDifference) - 1, '~')
                << std::endl;
        } else if (error.startLine != lineIndex - 1 && error.endLine == lineIndex - 1) {
            auto lastSize = line.size();
            ltrim(line);
            auto difference = lastSize - line.size();
            out << "  " << std::string(biggestNumber.size(), ' ') << " | "
                << std::string(difference, ' ')
                << std::string(1, '^')
                << std::string((endLineDifference - difference) - 1, '~')
                << std::endl;
        } else if (error.startLine < lineIndex - 1 && error.endLine > lineIndex - 1) {
            auto lastSize = line.size();
            ltrim(line);
            auto difference = lastSize - line.size();
            out << "  " << std::string(biggestNumber.size(), ' ') << " | "
                << std::string(difference, ' ')
                << std::string(1, '^')
                << std::string(line.size() - 1, '~')
                << std::endl;
        }
    }

    return out;
}