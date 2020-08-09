//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_UTILS_H
#define ARKOICOMPILER_UTILS_H

#include <algorithm>
#include <cstring>
#include <cstdarg>
#include <memory>
#include <vector>

class Utils {

public:
    Utils() = delete;

public:
    static void split(const std::string &input, std::vector<std::string> &list, char delimiter = ' ') {
        std::size_t current, previous = 0;
        current = input.find(delimiter);

        while (current != std::string::npos) {
            list.push_back(input.substr(previous, current - previous));
            previous = current + 1;
            current = input.find(delimiter, previous);
        }

        list.push_back(input.substr(previous, current - previous));
    }

    static void ltrim(std::string &input) {
        input.erase(input.begin(), std::find_if(input.begin(), input.end(), [](int current) {
            return !std::isspace(current);
        }));
    }

    static void rtrim(std::string &input) {
        input.erase(std::find_if(input.rbegin(), input.rend(), [](int current) {
            return !std::isspace(current);
        }).base(), input.end());
    }

    static void trim(std::string &input) {
        ltrim(input);
        rtrim(input);
    }

    static constexpr unsigned int hash(const char *string, int index = 0) {
        return !string[index] ? 5381 : (hash(string, index + 1) * 33) ^ string[index];
    }

};

#endif //ARKOICOMPILER_UTILS_H
