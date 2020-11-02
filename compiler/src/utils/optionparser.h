//
// Created by timo on 9/19/20.
//
// Reference: http://schneegans.github.io/tutorials/2019/08/06/commandline

#pragma once

#include <functional>
#include <iostream>
#include <cstdint>
#include <variant>
#include <string>
#include <vector>

class OptionParser {

public:
    typedef std::variant<int32_t*,
                        uint32_t*,
                        double*,
                        float*,
                        bool*,
                        std::string*> OptionValue;

public:
    struct Argument {
        std::function<void(const std::string &)> callBack;
        std::vector<std::string> m_Flags;
        OptionValue m_Value;
        std::string m_Help;
    };

private:
    std::vector<Argument> m_Arguments;
    std::string m_Description;

public:
    explicit OptionParser(std::string description);

public:
    void addArgument(const std::vector<std::string> &flags,
                     const OptionParser::OptionValue &value,
                     const std::string &help,
                     const std::function<void(const std::string&)> &callBack = nullptr);

    void printHelp(std::ostream &os = std::cout);

    void parse(int argc, char *argv[]);

};


