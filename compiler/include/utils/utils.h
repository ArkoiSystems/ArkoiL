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

namespace utils {

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

    // https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#C++
    template<typename T>
    typename T::size_type levensteinDistance(const T &source,
                                             const T &target,
                                             typename T::size_type insert_cost = 1,
                                             typename T::size_type delete_cost = 1,
                                             typename T::size_type replace_cost = 1) {
        if (source.size() > target.size())
            return levensteinDistance(target, source, delete_cost, insert_cost, replace_cost);

        using TSizeType = typename T::size_type;
        const TSizeType min_size = source.size(), max_size = target.size();
        std::vector<TSizeType> lev_dist(min_size + 1);

        lev_dist[0] = 0;
        for (TSizeType i = 1; i <= min_size; ++i) {
            lev_dist[i] = lev_dist[i - 1] + delete_cost;
        }

        for (TSizeType j = 1; j <= max_size; ++j) {
            TSizeType previous_diagonal = lev_dist[0], previous_diagonal_save;
            lev_dist[0] += insert_cost;

            for (TSizeType i = 1; i <= min_size; ++i) {
                previous_diagonal_save = lev_dist[i];
                if (source[i - 1] == target[j - 1]) {
                    lev_dist[i] = previous_diagonal;
                } else {
                    lev_dist[i] = std::min(
                            std::min(lev_dist[i - 1] + delete_cost, lev_dist[i] + insert_cost),
                            previous_diagonal + replace_cost);
                }
                previous_diagonal = previous_diagonal_save;
            }
        }

        return lev_dist[min_size];
    }

}