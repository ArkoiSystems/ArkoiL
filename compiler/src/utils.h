//
// Created by timo on 7/29/20.
//

#pragma once

#include <memory>
#include <vector>

#define GETSET(type, prefix, var)       \
    private:                            \
        type prefix##var;               \
                                        \
    public:                             \
        const type &get##var()          \
        {                               \
            return prefix##var;         \
        }                               \
                                        \
        void set##var(const type &val)  \
        {                               \
            prefix##var = val;          \
        }

namespace Utils {

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

    static constexpr unsigned int hash(const char *string, int index = 0) {
        return !string[index] ? 5381 : (hash(string, index + 1) * 33) ^ string[index];
    }

    template<typename Type>
    static std::pair<bool, int> indexOf(const std::vector<Type> &vector, const Type &element) {
        std::pair<bool, int> result;

        auto iterator = std::find(vector.begin(), vector.end(), element);
        if (iterator != vector.end()) {
            result.second = distance(vector.begin(), iterator);
            result.first = true;
        } else {
            result.first = false;
            result.second = -1;
        }

        return result;
    }

}