//
// Created by timo on 7/29/20.
//

#pragma once

#include <algorithm>
#include <memory>
#include <vector>
#include <any>

#include "../parser/allnodes.h"

#define DEFER_1(x, y) x##y
#define DEFER_2(x, y) DEFER_1(x, y)
#define DEFER_3(x)    DEFER_2(x, __COUNTER__)
#define defer(code)   auto DEFER_3(_defer_) = defer_func([&](){code;})

template<typename Function>
class DeferStruct {

private:
    Function function;

public:
    explicit DeferStruct(Function function) : function(function) {}

    ~DeferStruct() { function(); }

};

template<typename Function>
DeferStruct<Function> defer_func(Function function) {
    return DeferStruct<Function>(function);
}

namespace Utils {

    static void split(const std::string &input, std::vector<std::string> &list,
                      char delimiter = ' ') {
        std::size_t current, previous = 0;
        current = input.find(delimiter);

        while (current != std::string::npos) {
            list.emplace_back(input.substr(previous, current - previous));
            previous = current + 1;
            current = input.find(delimiter, previous);
        }

        list.emplace_back(input.substr(previous, current - previous));
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

    template<typename Container, typename Type>
    static std::pair<bool, int> indexOf(const Container &vector, const Type &element) {
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

    static std::string random_string(unsigned long length) {
        auto random_char = []() -> char {
            static const char charset[] =
                    "0123456789"
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    "abcdefghijklmnopqrstuvwxyz";
            static const unsigned long max_index = (sizeof(charset) - 1);
            return charset[rand() % max_index];
        };

        std::string string(length, 0);
        std::generate_n(string.begin(), length, random_char);
        return string;
    }

    static std::pair<bool, int> indexOf(const std::vector<SharedASTNode> &vector,
                                        const SharedASTNode &element) {
        std::pair<bool, int> result;

        for (int index = 0; index < vector.size(); index++) {
            auto node = vector.at(index);
            if (node != element)
                continue;

            result.first = true;
            result.second = index;
            return result;
        }

        result.first = false;
        result.second = -1;

        return result;
    }

}