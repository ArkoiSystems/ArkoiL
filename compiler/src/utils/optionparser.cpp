//
// Created by timo on 9/19/20.
//

#include "../../include/utils/optionparser.h"

#include <sstream>
#include <iomanip>

OptionParser::OptionParser(std::string description)
        : m_Description(std::move(description)) {}

void OptionParser::addArgument(const std::vector<std::string> &flags,
                               const OptionParser::OptionValue &value,
                               const std::string &help,
                               const std::function<void(const std::string&)> &callBack) {
    m_Arguments.push_back(Argument{callBack, flags, value, help});
}

void OptionParser::printHelp(std::ostream &os) {
    os << m_Description << std::endl;

    auto maxFlagLength = 0u;
    for (auto const &argument : m_Arguments) {
        auto flagLength = 0u;
        for (auto const &flag : argument.m_Flags)
            flagLength += flag.size() + 2;
        maxFlagLength = std::max(maxFlagLength, flagLength);
    }

    for (auto const &argument : m_Arguments) {
        std::string flags;
        for (auto const &flag : argument.m_Flags)
            flags += flag + ", ";

        std::stringstream stringStream;
        stringStream << std::left << std::setw(maxFlagLength) << flags.substr(0, flags.size() - 2);

        size_t spacePos = 0;
        size_t lineWidth = 0;
        while (spacePos != std::string::npos) {
            size_t nextSpacePos = argument.m_Help.find_first_of(' ', spacePos + 1);
            stringStream << argument.m_Help.substr(spacePos, nextSpacePos - spacePos);
            lineWidth += nextSpacePos - spacePos;
            spacePos = nextSpacePos;

            if (lineWidth > 60) {
                os << stringStream.str() << std::endl;
                stringStream = std::stringstream();
                stringStream << std::left << std::setw(maxFlagLength - 1) << " ";
                lineWidth = 0;
            }
        }
    }
}

void OptionParser::parse(int argc, char **argv) {
    auto index = 1u;
    while (index < argc) {
        std::string flag(argv[index]);
        std::string value;
        bool isSeparated;

        auto equalPos = flag.find('=');
        if (equalPos != std::string::npos) {
            value = flag.substr(equalPos + 1);
            flag = flag.substr(0, equalPos);
        } else if (index + 1 < argc) {
            value = argv[index + 1];
            isSeparated = true;
        }

        bool foundArgument = false;
        for (auto const &argument : m_Arguments) {
            if (std::find(argument.m_Flags.begin(), argument.m_Flags.end(), flag) == argument.m_Flags.end())
                continue;

            foundArgument = true;

            if(argument.callBack) {
                argument.callBack(value);
                break;
            }

            if (std::holds_alternative<bool *>(argument.m_Value)) {
                if (!value.empty() && value != "true" && value != "false")
                    isSeparated = false;
                *std::get<bool *>(argument.m_Value) = value != "false";
            } else if (std::holds_alternative<std::string *>(argument.m_Value)) {
                *std::get<std::string *>(argument.m_Value) = value;
            } else if (value.empty()) {
                throw std::runtime_error("Failed to parse command line arguments: "
                                         "Missing value for argument \"" + flag + "\"!");
            } else {
                std::visit([&value](auto &&arg) {
                    std::stringstream stringStream(value);
                    stringStream >> *arg;
                }, argument.m_Value);
            }

            break;
        }

        if (!foundArgument)
            std::cerr << "Ignoring unknown command line argument \"" << flag << "\"." << std::endl;

        if (foundArgument && isSeparated)
            ++index;
        ++index;
    }
}
